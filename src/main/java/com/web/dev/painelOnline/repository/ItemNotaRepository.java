package com.web.dev.painelOnline.repository;

import com.web.dev.painelOnline.entities.ItemNota;
import com.web.dev.painelOnline.entities.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ItemNotaRepository extends JpaRepository<ItemNota, Long> {

    // Busca itens por transação
    List<ItemNota> findByTransacao(Transacao transacao);

    // Busca itens por ID da transação
    List<ItemNota> findByTransacaoId(Long transacaoId);

    // Busca itens por descrição
    @Query("SELECT i FROM ItemNota i WHERE LOWER(i.descricao) LIKE LOWER(CONCAT('%', :descricao, '%'))")
    List<ItemNota> findByDescricaoContaining(@Param("descricao") String descricao);

    // Calcula o valor total dos itens de uma transação
    @Query("SELECT COALESCE(SUM(i.valorTotal), 0) FROM ItemNota i WHERE i.transacao.id = :transacaoId")
    BigDecimal calcularValorTotalPorTransacao(@Param("transacaoId") Long transacaoId);

    // Conta os itens por transação
    Long countByTransacaoId(Long transacaoId);

    // Busca os itens com valor acima de um determinado valor
    @Query("SELECT i FROM ItemNota i WHERE i.valorUnitario >= :valorMinimo")
    List<ItemNota> findByValorUnitarioGreaterThanEqual(@Param("valorMinimo") BigDecimal valorMinimo);

    // Busca itens ordenados por valor total decrescente
    @Query("SELECT i FROM ItemNota i WHERE i.transacao.id = :transacaoId ORDER BY i.valorTotal DESC")
    List<ItemNota> findByTransacaoIdOrderByValorTotalDesc(@Param("transacaoId") Long transacaoId);
}