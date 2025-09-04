package com.web.dev.painelOnline.Enum;

public enum TipoPagamento {
    A_VISTA("À Vista"),
    A_PRAZO("À Prazo"),
    PARCELADO("Parcelado");

    private final String descricao;

    TipoPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}