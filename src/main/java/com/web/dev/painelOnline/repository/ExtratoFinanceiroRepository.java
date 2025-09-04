package com.web.dev.painelOnline.repository;

import com.web.dev.painelOnline.entities.ExtratoFinanceiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExtratoFinanceiroRepository extends JpaRepository<ExtratoFinanceiro, Long> {

    // Buscar extrato por data específica
    Optional<ExtratoFinanceiro> findByData(LocalDate data);

    // Buscar extratos por período
    List<ExtratoFinanceiro> findByDataBetween(LocalDate dataInicio, LocalDate dataFim);

    // Verificar se existe extrato para uma data
    boolean existsByData(LocalDate data);

    // Buscar extratos do mês
    @Query("SELECT e FROM ExtratoFinanceiro e WHERE " +
            "YEAR(e.data) = :ano AND MONTH(e.data) = :mes " +
            "ORDER BY e.data ASC")
    List<ExtratoFinanceiro> findExtratosPorMes(@Param("ano") int ano, @Param("mes") int mes);

    // Buscar extratos do ano
    @Query("SELECT e FROM ExtratoFinanceiro e WHERE " +
            "YEAR(e.data) = :ano " +
            "ORDER BY e.data ASC")
    List<ExtratoFinanceiro> findExtratosPorAno(@Param("ano") int ano);

    // Calcular saldo acumulado até uma data
    @Query("SELECT COALESCE(SUM(e.saldoDiaDolares), 0) FROM ExtratoFinanceiro e WHERE e.data <= :data")
    BigDecimal calcularSaldoAcumuladoAteData(@Param("data") LocalDate data);

    // Buscar último extrato disponível
    @Query("SELECT e FROM ExtratoFinanceiro e ORDER BY e.data DESC")
    List<ExtratoFinanceiro> findUltimoExtrato();

    // Buscar extrato mais recente até uma data específica
    @Query("SELECT e FROM ExtratoFinanceiro e WHERE e.data <= :data ORDER BY e.data DESC")
    List<ExtratoFinanceiro> findExtratoMaisRecenteAteData(@Param("data") LocalDate data);

    // Calcular total de créditos do mês
    @Query("SELECT COALESCE(SUM(e.totalCreditosDolares), 0) FROM ExtratoFinanceiro e WHERE " +
            "YEAR(e.data) = :ano AND MONTH(e.data) = :mes")
    BigDecimal calcularTotalCreditosMes(@Param("ano") int ano, @Param("mes") int mes);

    // Calcular total de débitos do mês
    @Query("SELECT COALESCE(SUM(e.totalDebitosDolares), 0) FROM ExtratoFinanceiro e WHERE " +
            "YEAR(e.data) = :ano AND MONTH(e.data) = :mes")
    BigDecimal calcularTotalDebitosMes(@Param("ano") int ano, @Param("mes") int mes);

    // Calcular saldo do mês
    @Query("SELECT COALESCE(SUM(e.saldoDiaDolares), 0) FROM ExtratoFinanceiro e WHERE " +
            "YEAR(e.data) = :ano AND MONTH(e.data) = :mes")
    BigDecimal calcularSaldoMes(@Param("ano") int ano, @Param("mes") int mes);

    // Calcular total de créditos do ano
    @Query("SELECT COALESCE(SUM(e.totalCreditosDolares), 0) FROM ExtratoFinanceiro e WHERE " +
            "YEAR(e.data) = :ano")
    BigDecimal calcularTotalCreditosAno(@Param("ano") int ano);

    // Calcular total de débitos do ano
    @Query("SELECT COALESCE(SUM(e.totalDebitosDolares), 0) FROM ExtratoFinanceiro e WHERE " +
            "YEAR(e.data) = :ano")
    BigDecimal calcularTotalDebitosAno(@Param("ano") int ano);

    // Calcular saldo do ano
    @Query("SELECT COALESCE(SUM(e.saldoDiaDolares), 0) FROM ExtratoFinanceiro e WHERE " +
            "YEAR(e.data) = :ano")
    BigDecimal calcularSaldoAno(@Param("ano") int ano);

    // Buscar dias com saldo negativo
    @Query("SELECT e FROM ExtratoFinanceiro e WHERE e.saldoDiaDolares < 0 ORDER BY e.data DESC")
    List<ExtratoFinanceiro> findDiasComSaldoNegativo();

    // Buscar dias com maior movimento (créditos + débitos)
    @Query("SELECT e FROM ExtratoFinanceiro e ORDER BY (e.totalCreditosDolares + e.totalDebitosDolares) DESC")
    List<ExtratoFinanceiro> findDiasComMaiorMovimento();
}
