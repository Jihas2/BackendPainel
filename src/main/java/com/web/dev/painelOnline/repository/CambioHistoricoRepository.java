package com.web.dev.painelOnline.repository;

import com.web.dev.painelOnline.entities.CambioHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CambioHistoricoRepository extends JpaRepository<CambioHistorico, Long> {

    // Buscar taxa de câmbio por data específica
    Optional<CambioHistorico> findByData(LocalDate data);

    // Buscar taxas por período
    List<CambioHistorico> findByDataBetween(LocalDate dataInicio, LocalDate dataFim);

    // Buscar última taxa disponível
    @Query("SELECT c FROM CambioHistorico c ORDER BY c.data DESC")
    List<CambioHistorico> findUltimaTaxa();

    // Buscar taxa mais recente até uma data específica
    @Query("SELECT c FROM CambioHistorico c WHERE c.data <= :data ORDER BY c.data DESC")
    List<CambioHistorico> findTaxaMaisRecenteAteData(@Param("data") LocalDate data);

    // Verificar se existe taxa para uma data
    boolean existsByData(LocalDate data);

    // Buscar taxas do mês
    @Query("SELECT c FROM CambioHistorico c WHERE " +
            "YEAR(c.data) = :ano AND MONTH(c.data) = :mes " +
            "ORDER BY c.data ASC")
    List<CambioHistorico> findTaxasPorMes(@Param("ano") int ano, @Param("mes") int mes);

    // Buscar taxas do ano
    @Query("SELECT c FROM CambioHistorico c WHERE " +
            "YEAR(c.data) = :ano " +
            "ORDER BY c.data ASC")
    List<CambioHistorico> findTaxasPorAno(@Param("ano") int  ano);

    // Buscar primeiras N taxas mais recentes
    @Query("SELECT c FROM CambioHistorico c ORDER BY c.data DESC")
    List<CambioHistorico> findTopNByOrderByDataDesc();
}