package com.web.dev.painelOnline.services;

import com.web.dev.painelOnline.entities.ExtratoFinanceiro;
import com.web.dev.painelOnline.repository.ExtratoFinanceiroRepository;
import com.web.dev.painelOnline.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExtratoFinanceiroService {

    @Autowired
    private ExtratoFinanceiroRepository extratoFinanceiroRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    // Atualizar extrato de um dia específico
    public ExtratoFinanceiro atualizarExtratoDia(LocalDate data) {
        // Buscar ou criar extrato do dia
        Optional<ExtratoFinanceiro> extratoExistente = extratoFinanceiroRepository.findByData(data);
        ExtratoFinanceiro extrato = extratoExistente.orElse(new ExtratoFinanceiro(data));

        // Calcular totais do dia
        BigDecimal totalCreditos = transacaoRepository.calcularTotalCreditosPorData(data);
        BigDecimal totalDebitos = transacaoRepository.calcularTotalDebitosPorData(data);

        // Atualizar valores
        extrato.setTotalCreditosDolares(totalCreditos);
        extrato.setTotalDebitosDolares(totalDebitos);

        // Calcular saldo do dia explicitamente
        BigDecimal saldoDia = totalCreditos.subtract(totalDebitos);
        extrato.setSaldoDiaDolares(saldoDia);

        // Calcular e definir saldo acumulado
        BigDecimal saldoAcumuladoAnterior = calcularSaldoAcumuladoAteData(data.minusDays(1));
        extrato.setSaldoAcumuladoDolares(saldoAcumuladoAnterior.add(saldoDia));

        return extratoFinanceiroRepository.save(extrato);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoAcumuladoAteData(LocalDate data) {
        return extratoFinanceiroRepository.calcularSaldoAcumuladoAteData(data);
    }

    @Transactional(readOnly = true)
    public Optional<ExtratoFinanceiro> buscarExtratoPorData(LocalDate data) {
        return extratoFinanceiroRepository.findByData(data);
    }

    @Transactional(readOnly = true)
    public List<ExtratoFinanceiro> buscarExtratosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return extratoFinanceiroRepository.findByDataBetween(dataInicio, dataFim);
    }

    @Transactional(readOnly = true)
    public List<ExtratoFinanceiro> buscarExtratosMes(int ano, int mes) {
        return extratoFinanceiroRepository.findExtratosPorMes(ano, mes);
    }

    @Transactional(readOnly = true)
    public List<ExtratoFinanceiro> buscarExtratosAno(int ano) {
        return extratoFinanceiroRepository.findExtratosPorAno(ano);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalCreditosMes(int ano, int mes) {
        return extratoFinanceiroRepository.calcularTotalCreditosMes(ano, mes);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalDebitosMes(int ano, int mes) {
        return extratoFinanceiroRepository.calcularTotalDebitosMes(ano, mes);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoMes(int ano, int mes) {
        return extratoFinanceiroRepository.calcularSaldoMes(ano, mes);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalCreditosAno(int ano) {
        return extratoFinanceiroRepository.calcularTotalCreditosAno(ano);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalDebitosAno(int ano) {
        return extratoFinanceiroRepository.calcularTotalDebitosAno(ano);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoAno(int ano) {
        return extratoFinanceiroRepository.calcularSaldoAno(ano);
    }

    // Regenerar extratos de um período (útil para recalcular histórico)
    public void regenerarExtratosPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        LocalDate dataAtual = dataInicio;

        while (!dataAtual.isAfter(dataFim)) {
            atualizarExtratoDia(dataAtual);
            dataAtual = dataAtual.plusDays(1);
        }
    }
}
