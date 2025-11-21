package com.web.dev.painelOnline.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.dev.painelOnline.entities.CambioHistorico;
import com.web.dev.painelOnline.repository.CambioHistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CambioHistoricoService {

    @Autowired
    private CambioHistoricoRepository cambioHistoricoRepository;

    @Value("${cambio.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    //  Atualiza taxa USD → BRL usando API

    public BigDecimal atualizarTaxaCambioDoDia() {
        try {
            // Chamada da API
            String response = restTemplate.getForObject(apiUrl, String.class);

            JsonNode json = objectMapper.readTree(response);
            JsonNode usdBrl = json.get("USDBRL");

            BigDecimal taxa = new BigDecimal(usdBrl.get("bid").asText());
            LocalDate hoje = LocalDate.now();

            // Salva ou atualiza no histórico
            salvarTaxaCambio(hoje, taxa);

            return taxa;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar taxa de câmbio via API: " + e.getMessage());
        }
    }

    // Retorna taxa do dia (se existir no banco)

    public Optional<CambioHistorico> buscarTaxaDoDia() {
        return buscarTaxaPorData(LocalDate.now());
    }

    public CambioHistorico salvarTaxaCambio(LocalDate data, BigDecimal taxa) {
        Optional<CambioHistorico> taxaExistente = cambioHistoricoRepository.findByData(data);

        if (taxaExistente.isPresent()) {
            CambioHistorico cambio = taxaExistente.get();
            cambio.setTaxaUsdBrl(taxa);
            return cambioHistoricoRepository.save(cambio);
        } else {
            CambioHistorico novoCambio = new CambioHistorico(data, taxa);
            return cambioHistoricoRepository.save(novoCambio);
        }
    }

    @Transactional(readOnly = true)
    public Optional<CambioHistorico> buscarTaxaPorData(LocalDate data) {
        return cambioHistoricoRepository.findByData(data);
    }

    @Transactional(readOnly = true)
    public Optional<CambioHistorico> buscarUltimaTaxa() {
        List<CambioHistorico> taxas = cambioHistoricoRepository.findUltimaTaxa();
        return taxas.isEmpty() ? Optional.empty() : Optional.of(taxas.get(0));
    }

    @Transactional(readOnly = true)
    public Optional<CambioHistorico> buscarTaxaMaisRecenteAteData(LocalDate data) {
        List<CambioHistorico> taxas = cambioHistoricoRepository.findTaxaMaisRecenteAteData(data);
        return taxas.isEmpty() ? Optional.empty() : Optional.of(taxas.get(0));
    }

    @Transactional(readOnly = true)
    public List<CambioHistorico> buscarTaxasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return cambioHistoricoRepository.findByDataBetween(dataInicio, dataFim);
    }

    @Transactional(readOnly = true)
    public List<CambioHistorico> buscarTaxasMes(int ano, int mes) {
        return cambioHistoricoRepository.findTaxasPorMes(ano, mes);
    }

    public void excluirTaxa(Long id) {
        cambioHistoricoRepository.deleteById(id);
    }
}
