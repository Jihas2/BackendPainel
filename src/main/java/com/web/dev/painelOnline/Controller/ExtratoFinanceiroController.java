package com.web.dev.painelOnline.Controller;

import com.web.dev.painelOnline.entities.ExtratoFinanceiro;
import com.web.dev.painelOnline.services.ExtratoFinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/extratos")
@CrossOrigin(origins = "*")
public class ExtratoFinanceiroController {

    @Autowired
    private ExtratoFinanceiroService extratoFinanceiroService;

    // Buscar extrato por data específica
    @GetMapping("/data/{data}")
    public ResponseEntity<ExtratoFinanceiro> buscarExtratoPorData(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        Optional<ExtratoFinanceiro> extrato = extratoFinanceiroService.buscarExtratoPorData(data);
        return extrato.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar extratos por período
    @GetMapping("/periodo")
    public ResponseEntity<List<ExtratoFinanceiro>> buscarExtratosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<ExtratoFinanceiro> extratos = extratoFinanceiroService.buscarExtratosPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(extratos);
    }

    // Buscar extratos do mês
    @GetMapping("/mes/{ano}/{mes}")
    public ResponseEntity<List<ExtratoFinanceiro>> buscarExtratosMes(
            @PathVariable int ano,
            @PathVariable int mes) {
        List<ExtratoFinanceiro> extratos = extratoFinanceiroService.buscarExtratosMes(ano, mes);
        return ResponseEntity.ok(extratos);
    }

    // Buscar extratos do ano
    @GetMapping("/ano/{ano}")
    public ResponseEntity<List<ExtratoFinanceiro>> buscarExtratosAno(@PathVariable int ano) {
        List<ExtratoFinanceiro> extratos = extratoFinanceiroService.buscarExtratosAno(ano);
        return ResponseEntity.ok(extratos);
    }

    // Obter resumo mensal
    @GetMapping("/resumo/mes/{ano}/{mes}")
    public ResponseEntity<Map<String, Object>> obterResumoMensal(
            @PathVariable int ano,
            @PathVariable int mes) {

        Map<String, Object> resumo = new HashMap<>();

        BigDecimal totalCreditos = extratoFinanceiroService.calcularTotalCreditosMes(ano, mes);
        BigDecimal totalDebitos = extratoFinanceiroService.calcularTotalDebitosMes(ano, mes);
        BigDecimal saldoMes = extratoFinanceiroService.calcularSaldoMes(ano, mes);

        resumo.put("ano", ano);
        resumo.put("mes", mes);
        resumo.put("totalCreditos", totalCreditos);
        resumo.put("totalDebitos", totalDebitos);
        resumo.put("saldoMes", saldoMes);

        return ResponseEntity.ok(resumo);
    }

    // Obter resumo anual
    @GetMapping("/resumo/ano/{ano}")
    public ResponseEntity<Map<String, Object>> obterResumoAnual(@PathVariable int ano) {
        Map<String, Object> resumo = new HashMap<>();

        BigDecimal totalCreditos = extratoFinanceiroService.calcularTotalCreditosAno(ano);
        BigDecimal totalDebitos = extratoFinanceiroService.calcularTotalDebitosAno(ano);
        BigDecimal saldoAno = extratoFinanceiroService.calcularSaldoAno(ano);

        resumo.put("ano", ano);
        resumo.put("totalCreditos", totalCreditos);
        resumo.put("totalDebitos", totalDebitos);
        resumo.put("saldoAno", saldoAno);

        return ResponseEntity.ok(resumo);
    }

    // Atualizar extrato de um dia específico
    @PostMapping("/atualizar/{data}")
    public ResponseEntity<ExtratoFinanceiro> atualizarExtratoDia(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        try {
            ExtratoFinanceiro extrato = extratoFinanceiroService.atualizarExtratoDia(data);
            return ResponseEntity.ok(extrato);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Regenerar extratos de um período
    @PostMapping("/regenerar")
    public ResponseEntity<String> regenerarExtratosPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            extratoFinanceiroService.regenerarExtratosPeriodo(dataInicio, dataFim);
            return ResponseEntity.ok("Extratos regenerados com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao regenerar extratos: " + e.getMessage());
        }
    }

    // Calcular saldo acumulado até uma data
    @GetMapping("/saldo-acumulado/{data}")
    public ResponseEntity<Map<String, Object>> calcularSaldoAcumulado(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        BigDecimal saldoAcumulado = extratoFinanceiroService.calcularSaldoAcumuladoAteData(data);

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("saldoAcumulado", saldoAcumulado);

        return ResponseEntity.ok(response);
    }
}
