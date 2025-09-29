package com.web.dev.painelOnline.repository;

import com.web.dev.painelOnline.entities.Transacao;
import com.web.dev.painelOnline.Enum.TipoTransacao;
import com.web.dev.painelOnline.Enum.StatusPagamento;
import com.web.dev.painelOnline.Enum.TipoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {


    List<Transacao> findByData(LocalDate data);

    List<Transacao> findByDataBetween(LocalDate dataInicio, LocalDate dataFim);

    // Busca transações por tipo
    List<Transacao> findByTipoTransacao(TipoTransacao tipoTransacao);

    // Busca transações por status
    List<Transacao> findByStatusPagamento(StatusPagamento statusPagamento);

    // Busca transações por tipo de pagamento
    List<Transacao> findByTipoPagamento(TipoPagamento tipoPagamento);

    // Busca transações por período e tipo
    List<Transacao> findByDataBetweenAndTipoTransacao(
            LocalDate dataInicio,
            LocalDate dataFim,
            TipoTransacao tipoTransacao
    );

    // Busca transações por período e status
    List<Transacao> findByDataBetweenAndStatusPagamento(
            LocalDate dataInicio,
            LocalDate dataFim,
            StatusPagamento statusPagamento
    );

    // Busca débitos à prazo do mes
    @Query("SELECT t FROM Transacao t WHERE " +
            "YEAR(t.data) = :ano AND MONTH(t.data) = :mes AND " +
            "t.tipoTransacao = 'DEBITO' AND t.tipoPagamento = 'A_PRAZO'")
    List<Transacao> findDebitosAPrazoPorMes(@Param("ano") int ano, @Param("mes") int mes);

    // Busca transações do mes
    @Query("SELECT t FROM Transacao t WHERE " +
            "YEAR(t.data) = :ano AND MONTH(t.data) = :mes " +
            "ORDER BY t.data DESC")
    List<Transacao> findTransacoesPorMes(@Param("ano") int ano, @Param("mes") int mes);

    // Busca transações do ano
    @Query("SELECT t FROM Transacao t WHERE " +
            "YEAR(t.data) = :ano " +
            "ORDER BY t.data DESC")
    List<Transacao> findTransacoesPorAno(@Param("ano") int ano);

    // Calcula total de créditos em dólares por período
    @Query("SELECT COALESCE(SUM(t.valorDolares), 0) FROM Transacao t WHERE " +
            "t.data BETWEEN :dataInicio AND :dataFim AND t.tipoTransacao = 'CREDITO'")
    BigDecimal calcularTotalCreditosPorPeriodo(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    // Calcula total de débitos em dólares por período
    @Query("SELECT COALESCE(SUM(t.valorDolares), 0) FROM Transacao t WHERE " +
            "t.data BETWEEN :dataInicio AND :dataFim AND t.tipoTransacao = 'DEBITO'")
    BigDecimal calcularTotalDebitosPorPeriodo(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    // Calcula total de créditos em dólares por data específica
    @Query("SELECT COALESCE(SUM(t.valorDolares), 0) FROM Transacao t WHERE " +
            "t.data = :data AND t.tipoTransacao = 'CREDITO'")
    BigDecimal calcularTotalCreditosPorData(@Param("data") LocalDate data);

    // Calcula total de débitos em dólares por data específica
    @Query("SELECT COALESCE(SUM(t.valorDolares), 0) FROM Transacao t WHERE " +
            "t.data = :data AND t.tipoTransacao = 'DEBITO'")
    BigDecimal calcularTotalDebitosPorData(@Param("data") LocalDate data);

    // Busca transações com itens
    @Query("SELECT DISTINCT t FROM Transacao t LEFT JOIN FETCH t.itens WHERE t.id = :id")
    Transacao findTransacaoComItens(@Param("id") Long id);

    // Busca transações por característica
    @Query("SELECT t FROM Transacao t WHERE LOWER(t.caracteristica) LIKE LOWER(CONCAT('%', :caracteristica, '%'))")
    List<Transacao> findByCaracteristicaContaining(@Param("caracteristica") String caracteristica);

    // Dashboard: Total de pagamentos em dólares do mes
    @Query("SELECT COALESCE(SUM(t.valorDolares), 0) FROM Transacao t WHERE " +
            "YEAR(t.data) = :ano AND MONTH(t.data) = :mes AND " +
            "t.tipoTransacao = 'CREDITO' AND t.statusPagamento = 'PAGO'")
    BigDecimal calcularTotalPagamentosMes(@Param("ano") int ano, @Param("mes") int mes);

    // Dashboard: Total de débitos à prazo do mes
    @Query("SELECT COALESCE(SUM(t.valorDolares), 0) FROM Transacao t WHERE " +
            "YEAR(t.data) = :ano AND MONTH(t.data) = :mes AND " +
            "t.tipoTransacao = 'DEBITO' AND t.tipoPagamento = 'A_PRAZO'")
    BigDecimal calcularTotalDebitosPrazoMes(@Param("ano") int ano, @Param("mes") int mes);
}