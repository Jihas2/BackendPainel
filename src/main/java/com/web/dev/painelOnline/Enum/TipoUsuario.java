package com.web.dev.painelOnline.Enum;

public enum TipoUsuario {
    DEMANDANTE("Demandante"),
    USUARIO("Usuário");

    private final String descricao;

    TipoUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}