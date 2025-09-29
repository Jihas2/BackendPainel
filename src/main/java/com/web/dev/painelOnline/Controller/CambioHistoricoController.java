package com.web.dev.painelOnline.Controller;

import com.web.dev.painelOnline.entities.CambioHistorico;
import com.web.dev.painelOnline.services.CambioHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cambio")
@CrossOrigin(origins = "*")
public class CambioHistoricoController {

    @Autowired
    private CambioHistoricoService cambioHistoricoService;

    // Salva nova taxa de cambio
    @PostMapping
    public ResponseEntity<CambioHistorico> salvarTaxaCambio(@RequestBody CambioHistorico cambio) {
        try {
            CambioHistorico cambioSalvo = cambioHistoricoService.salvarTaxaCambio(
                    cambio.getData(),
                    cambio.getTaxaUsdBrl()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(cambioSalvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Busca a taxa por data específica
    @GetMapping("/data/{data}")
    public ResponseEntity<CambioHistorico> buscarTaxaPorData(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        Optional<CambioHistorico> cambio = cambioHistoricoService.buscarTaxaPorData(data);
        return cambio.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Busca a última taxa disponível
    @GetMapping("/ultima")
    public ResponseEntity<CambioHistorico> buscarUltimaTaxa() {
        Optional<CambioHistorico> cambio = cambioHistoricoService.buscarUltimaTaxa();
        return cambio.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Busca a taxa mais recente até uma data específica
    @GetMapping("/ate-data/{data}")
    public ResponseEntity<CambioHistorico> buscarTaxaMaisRecenteAteData(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        Optional<CambioHistorico> cambio = cambioHistoricoService.buscarTaxaMaisRecenteAteData(data);
        return cambio.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Busca as taxas por período
    @GetMapping("/periodo")
    public ResponseEntity<List<CambioHistorico>> buscarTaxasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<CambioHistorico> taxas = cambioHistoricoService.buscarTaxasPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(taxas);
    }

    // Busca as taxas do mes
    @GetMapping("/mes/{ano}/{mes}")
    public ResponseEntity<List<CambioHistorico>> buscarTaxasMes(
            @PathVariable int ano,
            @PathVariable int mes) {
        List<CambioHistorico> taxas = cambioHistoricoService.buscarTaxasMes(ano, mes);
        return ResponseEntity.ok(taxas);
    }

    // Exclui a taxa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirTaxa(@PathVariable Long id) {
        try {
            cambioHistoricoService.excluirTaxa(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
