package com.web.dev.painelOnline.Controller;

import com.web.dev.painelOnline.entities.CambioHistorico;
import com.web.dev.painelOnline.services.CambioHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cambio")
@CrossOrigin("*")
public class CambioHistoricoController {

    @Autowired
    private CambioHistoricoService cambioHistoricoService;

    // Atualiza taxa usando API

    @GetMapping("/atualizar")
    public BigDecimal atualizarTaxaDoDia() {
        return cambioHistoricoService.atualizarTaxaCambioDoDia();
    }

    // Buscar taxa do dia

    @GetMapping("/taxa-hoje")
    public Optional<CambioHistorico> buscarTaxaHoje() {
        return cambioHistoricoService.buscarTaxaDoDia();
    }

    // Salvar manualmente taxa (NAO SERA MAIS USADO MAS MANTI ELE)

    @PostMapping("/salvar")
    public CambioHistorico salvarTaxaCambio(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam BigDecimal taxa) {

        return cambioHistoricoService.salvarTaxaCambio(date, taxa);
    }

    // Buscar por data
    @GetMapping("/por-data")
    public Optional<CambioHistorico> buscarTaxaPorData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return cambioHistoricoService.buscarTaxaPorData(date);
    }

    // Última taxa cadastrada
    @GetMapping("/ultima")
    public Optional<CambioHistorico> buscarUltimaTaxa() {
        return cambioHistoricoService.buscarUltimaTaxa();
    }

    // Buscar taxa mais recente até uma data
    @GetMapping("/ate-data")
    public Optional<CambioHistorico> buscarTaxaMaisRecenteAteData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return cambioHistoricoService.buscarTaxaMaisRecenteAteData(date);
    }

    // Buscar período
    @GetMapping("/periodo")
    public List<CambioHistorico> buscarPeriodos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        return cambioHistoricoService.buscarTaxasPorPeriodo(inicio, fim);
    }

    // Buscar taxas do mês
    @GetMapping("/mes")
    public List<CambioHistorico> buscarMes(
            @RequestParam int ano,
            @RequestParam int mes) {

        return cambioHistoricoService.buscarTaxasMes(ano, mes);
    }

    // Excluir taxa
    @DeleteMapping("/excluir/{id}")
    public void excluirTaxa(@PathVariable Long id) {
        cambioHistoricoService.excluirTaxa(id);
    }
}
