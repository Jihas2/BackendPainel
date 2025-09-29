package com.web.dev.painelOnline.services;

import com.web.dev.painelOnline.entities.CambioHistorico;
import com.web.dev.painelOnline.repository.CambioHistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CambioHistoricoService {

    @Autowired
    private CambioHistoricoRepository cambioHistoricoRepository;

    // Salva nova taxa de cambio
    public CambioHistorico salvarTaxaCambio(LocalDate data, BigDecimal taxa) {
        // Verifica se já existe taxa para a data
        Optional<CambioHistorico> taxaExistente = cambioHistoricoRepository.findByData(data);

        if (taxaExistente.isPresent()) {
            // Atualiza taxa existente
            CambioHistorico cambio = taxaExistente.get();
            cambio.setTaxaUsdBrl(taxa);
            return cambioHistoricoRepository.save(cambio);
        } else {
            // Cria nova taxa
            CambioHistorico novoCambio = new CambioHistorico(data, taxa);
            return cambioHistoricoRepository.save(novoCambio);
        }
    }

    // Busca taxa por data específica
    @Transactional(readOnly = true)
    public Optional<CambioHistorico> buscarTaxaPorData(LocalDate data) {
        return cambioHistoricoRepository.findByData(data);
    }

    // Busca taxa mais recente disponível
    @Transactional(readOnly = true)
    public Optional<CambioHistorico> buscarUltimaTaxa() {
        List<CambioHistorico> taxas = cambioHistoricoRepository.findUltimaTaxa();
        return taxas.isEmpty() ? Optional.empty() : Optional.of(taxas.get(0));
    }

    // Busca taxa mais recente até uma data específica
    @Transactional(readOnly = true)
    public Optional<CambioHistorico> buscarTaxaMaisRecenteAteData(LocalDate data) {
        List<CambioHistorico> taxas = cambioHistoricoRepository.findTaxaMaisRecenteAteData(data);
        return taxas.isEmpty() ? Optional.empty() : Optional.of(taxas.get(0));
    }

    // Busca todas as taxas por período
    @Transactional(readOnly = true)
    public List<CambioHistorico> buscarTaxasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return cambioHistoricoRepository.findByDataBetween(dataInicio, dataFim);
    }

    // Busca taxas do mes
    @Transactional(readOnly = true)
    public List<CambioHistorico> buscarTaxasMes(int ano, int mes) {
        return cambioHistoricoRepository.findTaxasPorMes(ano, mes);
    }

    // Excluir taxa
    public void excluirTaxa(Long id) {
        cambioHistoricoRepository.deleteById(id);
    }
}