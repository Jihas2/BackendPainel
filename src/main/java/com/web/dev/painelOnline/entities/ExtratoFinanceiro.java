package com.web.dev.painelOnline.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "extrato_financeiro", uniqueConstraints = {
        @UniqueConstraint(columnNames = "data")
})
public class ExtratoFinanceiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate data;

    @Column(name = "total_creditos_dolares", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalCreditosDolares;

    @Column(name = "total_debitos_dolares", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalDebitosDolares;

    @Column(name = "saldo_dia_dolares", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoDiaDolares;

    @Column(name = "saldo_acumulado_dolares", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoAcumuladoDolares;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
        calculateSaldoDia();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
        calculateSaldoDia();
    }

    private void calculateSaldoDia() {
        if (this.totalCreditosDolares != null && this.totalDebitosDolares != null) {
            this.saldoDiaDolares = this.totalCreditosDolares.subtract(this.totalDebitosDolares);
        }
    }

    public ExtratoFinanceiro() {
        this.totalCreditosDolares = BigDecimal.ZERO;
        this.totalDebitosDolares = BigDecimal.ZERO;
        this.saldoDiaDolares = BigDecimal.ZERO;
        this.saldoAcumuladoDolares = BigDecimal.ZERO;
    }

    public ExtratoFinanceiro(LocalDate data) {
        this();
        this.data = data;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public BigDecimal getTotalCreditosDolares() { return totalCreditosDolares; }
    public void setTotalCreditosDolares(BigDecimal totalCreditosDolares) {
        this.totalCreditosDolares = totalCreditosDolares;
    }

    public BigDecimal getTotalDebitosDolares() { return totalDebitosDolares; }
    public void setTotalDebitosDolares(BigDecimal totalDebitosDolares) {
        this.totalDebitosDolares = totalDebitosDolares;
    }

    public BigDecimal getSaldoDiaDolares() { return saldoDiaDolares; }
    public void setSaldoDiaDolares(BigDecimal saldoDiaDolares) {
        this.saldoDiaDolares = saldoDiaDolares;
    }

    public BigDecimal getSaldoAcumuladoDolares() { return saldoAcumuladoDolares; }
    public void setSaldoAcumuladoDolares(BigDecimal saldoAcumuladoDolares) {
        this.saldoAcumuladoDolares = saldoAcumuladoDolares;
    }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
