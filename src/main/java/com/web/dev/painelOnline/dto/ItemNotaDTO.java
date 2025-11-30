package com.web.dev.painelOnline.dto;

import java.math.BigDecimal;

public class ItemNotaDTO {
    private Long id;
    private String descricao;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private Long transacaoId;

    public ItemNotaDTO() {}

    public ItemNotaDTO(Long id, String descricao, Integer quantidade, BigDecimal valorUnitario,
                       BigDecimal valorTotal, Long transacaoId) {
        this.id = id;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.valorTotal = valorTotal;
        this.transacaoId = transacaoId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public Long getTransacaoId() { return transacaoId; }
    public void setTransacaoId(Long transacaoId) { this.transacaoId = transacaoId; }
}