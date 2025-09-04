package com.web.dev.painelOnline.Enum;

public enum StatusPagamento {
    PENDENTE("Pendente"),
    PAGO("Pago"),
    CANCELADO("Cancelado"),
    VENCIDO("Vencido");

    private final String descricao;

    StatusPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}