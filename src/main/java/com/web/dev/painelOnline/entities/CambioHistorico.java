package com.web.dev.painelOnline.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cambio_historico", uniqueConstraints = {
        @UniqueConstraint(columnNames = "data")
})
public class CambioHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate data;

    @Column(name = "taxa_usd_brl", nullable = false, precision = 10, scale = 4)
    private BigDecimal taxaUsdBrl;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
    }

    // Constructors
    public CambioHistorico() {}

    public CambioHistorico(LocalDate data, BigDecimal taxaUsdBrl) {
        this.data = data;
        this.taxaUsdBrl = taxaUsdBrl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public BigDecimal getTaxaUsdBrl() { return taxaUsdBrl; }
    public void setTaxaUsdBrl(BigDecimal taxaUsdBrl) { this.taxaUsdBrl = taxaUsdBrl; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}