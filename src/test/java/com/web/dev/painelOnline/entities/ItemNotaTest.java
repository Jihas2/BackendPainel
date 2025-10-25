package com.web.dev.painelOnline.entities;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ItemNotaTest {

    @Test
    void calculateTotal_setsValorTotal() throws Exception {
        ItemNota i = new ItemNota();
        i.setQuantidade(3);
        i.setValorUnitario(BigDecimal.valueOf(12.50));

        Method calc = ItemNota.class.getDeclaredMethod("calculateTotal");
        calc.setAccessible(true);
        calc.invoke(i);

        assertEquals(BigDecimal.valueOf(37.50).setScale(2), i.getValorTotal().setScale(2));
    }

    @Test
    void toString_includesTransacaoInfo_whenPresent() {
        ItemNota i = new ItemNota();
        i.setDescricao("Produto X");
        i.setQuantidade(2);
        i.setValorUnitario(BigDecimal.valueOf(10));
        com.web.dev.painelOnline.entities.Transacao t = new com.web.dev.painelOnline.entities.Transacao();
        t.setId(123L);
        i.setTransacao(t);

        String s = i.toString();
        assertTrue(s.contains("Produto X"));
        assertTrue(s.contains("Transacao[id=123]") || s.contains("transacao"));
    }
}
