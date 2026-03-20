package com.web.dev.painelOnline.Controller;

import com.web.dev.painelOnline.entities.CambioHistorico;
import com.web.dev.painelOnline.services.CambioHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/atualizar")
    @PreAuthorize("permitAll()")
    public BigDecimal atualizarTaxaDoDia() {
        return cambioHistoricoService.atualizarTaxaCambioDoDia();
    }

    @GetMapping("/taxa-hoje")
    @PreAuthorize("permitAll()")
    public Optional<CambioHistorico> buscarTaxaHoje() {
        return cambioHistoricoService.buscarTaxaDoDia();
    }

    @PostMapping("/salvar")
    public CambioHistorico salvarTaxaCambio(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam BigDecimal taxa) {
        return cambioHistoricoService.salvarTaxaCambio(date, taxa);
    }

    @GetMapping("/por-data")
    public Optional<CambioHistorico> buscarTaxaPorData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return cambioHistoricoService.buscarTaxaPorData(date);
    }

    @GetMapping("/ultima")
    @PreAuthorize("permitAll()")
    public Optional<CambioHistorico> buscarUltimaTaxa() {
        return cambioHistoricoService.buscarUltimaTaxa();
    }

    @GetMapping("/ate-data")
    public Optional<CambioHistorico> buscarTaxaMaisRecenteAteData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return cambioHistoricoService.buscarTaxaMaisRecenteAteData(date);
    }

    @GetMapping("/periodo")
    public List<CambioHistorico> buscarPeriodos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return cambioHistoricoService.buscarTaxasPorPeriodo(inicio, fim);
    }

    @GetMapping("/mes")
    public List<CambioHistorico> buscarMes(
            @RequestParam int ano,
            @RequestParam int mes) {
        return cambioHistoricoService.buscarTaxasMes(ano, mes);
    }

    @DeleteMapping("/excluir/{id}")
    public void excluirTaxa(@PathVariable Long id) {
        cambioHistoricoService.excluirTaxa(id);
    }
}