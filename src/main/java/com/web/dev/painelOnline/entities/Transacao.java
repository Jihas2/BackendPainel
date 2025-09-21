package com.web.dev.painelOnline.entities;

import com.web.dev.painelOnline.Enum.TipoTransacao;
import com.web.dev.painelOnline.Enum.StatusPagamento;
import com.web.dev.painelOnline.Enum.TipoPagamento;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transacoes")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false, length = 500)
    private String caracteristica;

    @Column(name = "valor_reais", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorReais;

    @Column(name = "taxa_cambio", nullable = false, precision = 10, scale = 4)
    private BigDecimal taxaCambio;

    @Column(name = "valor_dolares", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorDolares;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transacao", nullable = false)
    private TipoTransacao tipoTransacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false)
    private StatusPagamento statusPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pagamento", nullable = false)
    private TipoPagamento tipoPagamento;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @OneToMany(mappedBy = "transacao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemNota> itens;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
        // Calcula valor em dólares de forma segura
        if (this.valorReais != null && this.taxaCambio != null) {
            this.valorDolares = this.valorReais.divide(this.taxaCambio, 2, RoundingMode.HALF_UP);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
        // Recalcula valor em dólares
        if (this.valorReais != null && this.taxaCambio != null) {
            this.valorDolares = this.valorReais.divide(this.taxaCambio, 2, RoundingMode.HALF_UP);
        }
    }

    // Constructors
    public Transacao() {}

    public Transacao(LocalDate data, String caracteristica, BigDecimal valorReais,
                     BigDecimal taxaCambio, TipoTransacao tipoTransacao,
                     StatusPagamento statusPagamento, TipoPagamento tipoPagamento) {
        this.data = data;
        this.caracteristica = caracteristica;
        this.valorReais = valorReais;
        this.taxaCambio = taxaCambio;
        this.tipoTransacao = tipoTransacao;
        this.statusPagamento = statusPagamento;
        this.tipoPagamento = tipoPagamento;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getCaracteristica() { return caracteristica; }
    public void setCaracteristica(String caracteristica) { this.caracteristica = caracteristica; }

    public BigDecimal getValorReais() { return valorReais; }
    public void setValorReais(BigDecimal valorReais) { this.valorReais = valorReais; }

    public BigDecimal getTaxaCambio() { return taxaCambio; }
    public void setTaxaCambio(BigDecimal taxaCambio) { this.taxaCambio = taxaCambio; }

    public BigDecimal getValorDolares() { return valorDolares; }
    public void setValorDolares(BigDecimal valorDolares) { this.valorDolares = valorDolares; }

    public TipoTransacao getTipoTransacao() { return tipoTransacao; }
    public void setTipoTransacao(TipoTransacao tipoTransacao) { this.tipoTransacao = tipoTransacao; }

    public StatusPagamento getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(StatusPagamento statusPagamento) { this.statusPagamento = statusPagamento; }

    public TipoPagamento getTipoPagamento() { return tipoPagamento; }
    public void setTipoPagamento(TipoPagamento tipoPagamento) { this.tipoPagamento = tipoPagamento; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public List<ItemNota> getItens() { return itens; }
    public void setItens(List<ItemNota> itens) { this.itens = itens; }

    @Override
    public String toString() {
        return "Transacao{" +
                "caracteristica='" + caracteristica + '\'' +
                ", id=" + id +
                ", data=" + data +
                ", valorReais=" + valorReais +
                ", taxaCambio=" + taxaCambio +
                ", valorDolares=" + valorDolares +
                ", tipoTransacao=" + tipoTransacao +
                ", statusPagamento=" + statusPagamento +
                ", tipoPagamento=" + tipoPagamento +
                ", dataCriacao=" + dataCriacao +
                ", dataAtualizacao=" + dataAtualizacao +
                ", itens=" + itens +
                '}';
    }
}
