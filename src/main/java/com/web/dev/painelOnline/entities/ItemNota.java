package com.web.dev.painelOnline.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_nota")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class ItemNota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "valor_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotal;

    // Permite que o campo seja **lido do JSON de entrada** (WRITE), mas evita que
    // seja automaticamente serializado na resposta (READ),
    // ao mesmo tempo não impede o uso JPA.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transacao_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private com.web.dev.painelOnline.entities.Transacao transacao;

    @PrePersist
    @PreUpdate
    protected void calculateTotal() {
        if (this.quantidade != null && this.valorUnitario != null) {
            this.valorTotal = this.valorUnitario.multiply(BigDecimal.valueOf(this.quantidade));
        }
    }

    // Constructors
    public ItemNota() {}

    public ItemNota(String descricao, Integer quantidade, BigDecimal valorUnitario, com.web.dev.painelOnline.entities.Transacao transacao) {
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.transacao = transacao;
    }

    // Getters and Setters
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

    public com.web.dev.painelOnline.entities.Transacao getTransacao() { return transacao; }
    public void setTransacao(com.web.dev.painelOnline.entities.Transacao transacao) { this.transacao = transacao; }

    @Override
    public String toString() {
        return "ItemNota{" +
                "descricao='" + descricao + '\'' +
                ", id=" + id +
                ", quantidade=" + quantidade +
                ", valorUnitario=" + valorUnitario +
                ", valorTotal=" + valorTotal +
                ", transacao=" + (transacao != null ? "Transacao[id=" + transacao.getId() + "]" : "null") +
                '}';
    }
}
