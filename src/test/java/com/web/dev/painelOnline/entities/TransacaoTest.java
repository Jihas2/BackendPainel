package com.web.dev.painelOnline.entities;

import com.web.dev.painelOnline.Enum.StatusPagamento;
import com.web.dev.painelOnline.Enum.TipoPagamento;
import com.web.dev.painelOnline.Enum.TipoTransacao;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransacaoTest {

    @Test
    void onCreate_calculatesValorDolares_withRounding() throws Exception {
        Transacao t = new Transacao(LocalDate.now(), "descr", BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(3.3333), TipoTransacao.CREDITO,
                StatusPagamento.PAGO, TipoPagamento.A_VISTA);

        Method onCreate = Transacao.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(t);

        assertNotNull(t.getValorDolares());
        assertEquals(2, t.getValorDolares().scale());
    }

    @Test
    void onUpdate_updatesDataAtualizacao_andRecalculates() throws Exception {
        Transacao t = new Transacao();
        t.setValorReais(BigDecimal.valueOf(200));
        t.setTaxaCambio(BigDecimal.valueOf(4));
        Method onUpdate = Transacao.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(t);
        assertNotNull(t.getDataAtualizacao());
        assertEquals(BigDecimal.valueOf(50.00).setScale(2), t.getValorDolares().setScale(2));
    }

    @Test
    void toString_containsFields() {
        Transacao t = new Transacao();
        t.setCaracteristica("teste");
        t.setValorReais(BigDecimal.valueOf(10));
        String s = t.toString();
        assertTrue(s.contains("teste") || s.contains("valorReais"));
    }
}
