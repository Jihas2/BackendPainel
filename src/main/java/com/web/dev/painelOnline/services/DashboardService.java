package com.web.dev.painelOnline.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private ExtratoFinanceiroService extratoFinanceiroService;

    // Obter dados do dashboard para o mês atual
    public Map<String, Object> obterDadosDashboard() {
        LocalDate dataAtual = LocalDate.now();
        return obterDadosDashboardMes(dataAtual.getYear(), dataAtual.getMonthValue());
    }

    // Obter dados do dashboard para um mês específico
    public Map<String, Object> obterDadosDashboardMes(int ano, int mes) {
        Map<String, Object> dashboard = new HashMap<>();

        // Total de pagamentos feitos em dólares no mês
        BigDecimal totalPagamentos = transacaoService.calcularTotalPagamentosMes(ano, mes);

        // Total de débitos à prazo em dólares no mês
        BigDecimal totalDebitosPrazo = transacaoService.calcularTotalDebitosPrazoMes(ano, mes);

        // Totais mensais do extrato
        BigDecimal totalCreditosMes = extratoFinanceiroService.calcularTotalCreditosMes(ano, mes);
        BigDecimal totalDebitosMes = extratoFinanceiroService.calcularTotalDebitosMes(ano, mes);
        BigDecimal saldoMes = extratoFinanceiroService.calcularSaldoMes(ano, mes);

        // Saldo acumulado até o final do mês
        LocalDate ultimoDiaMes = LocalDate.of(ano, mes, 1).plusMonths(1).minusDays(1);
        BigDecimal saldoAcumulado = extratoFinanceiroService.calcularSaldoAcumuladoAteData(ultimoDiaMes);

        dashboard.put("totalPagamentosMes", totalPagamentos);
        dashboard.put("totalDebitosPrazoMes", totalDebitosPrazo);
        dashboard.put("totalCreditosMes", totalCreditosMes);
        dashboard.put("totalDebitosMes", totalDebitosMes);
        dashboard.put("saldoMes", saldoMes);
        dashboard.put("saldoAcumulado", saldoAcumulado);
        dashboard.put("ano", ano);
        dashboard.put("mes", mes);

        return dashboard;
    }

    // Obter dados do dashboard para o ano
    public Map<String, Object> obterDadosDashboardAno(int ano) {
        Map<String, Object> dashboard = new HashMap<>();

        // Totais anuais do extrato
        BigDecimal totalCreditosAno = extratoFinanceiroService.calcularTotalCreditosAno(ano);
        BigDecimal totalDebitosAno = extratoFinanceiroService.calcularTotalDebitosAno(ano);
        BigDecimal saldoAno = extratoFinanceiroService.calcularSaldoAno(ano);

        // Saldo acumulado até o final do ano
        LocalDate ultimoDiaAno = LocalDate.of(ano, 12, 31);
        BigDecimal saldoAcumulado = extratoFinanceiroService.calcularSaldoAcumuladoAteData(ultimoDiaAno);

        dashboard.put("totalCreditosAno", totalCreditosAno);
        dashboard.put("totalDebitosAno", totalDebitosAno);
        dashboard.put("saldoAno", saldoAno);
        dashboard.put("saldoAcumulado", saldoAcumulado);
        dashboard.put("ano", ano);

        return dashboard;
    }
}