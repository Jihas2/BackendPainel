package com.web.dev.painelOnline.entities;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ExtratoFinanceiroTest {

    @Test
    void defaultConstructor_initializesZeros() {
        ExtratoFinanceiro e = new ExtratoFinanceiro();
        assertNotNull(e.getTotalCreditosDolares());
        assertNotNull(e.getTotalDebitosDolares());
        assertNotNull(e.getSaldoDiaDolares());
        assertEquals(BigDecimal.ZERO, e.getTotalCreditosDolares());
        assertEquals(BigDecimal.ZERO, e.getTotalDebitosDolares());
        assertEquals(BigDecimal.ZERO, e.getSaldoDiaDolares());
    }

    @Test
    void calculateSaldoDay_onCreateAndUpdate() throws Exception {
        ExtratoFinanceiro e = new ExtratoFinanceiro(LocalDate.now());
        e.setTotalCreditosDolares(BigDecimal.valueOf(300));
        e.setTotalDebitosDolares(BigDecimal.valueOf(120));

        Method onCreate = ExtratoFinanceiro.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(e);

        assertEquals(BigDecimal.valueOf(180).setScale(2), e.getSaldoDiaDolares().setScale(2));

        e.setTotalCreditosDolares(BigDecimal.valueOf(500));
        e.setTotalDebitosDolares(BigDecimal.valueOf(200));
        Method onUpdate = ExtratoFinanceiro.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(e);

        assertEquals(BigDecimal.valueOf(300).setScale(2), e.getSaldoDiaDolares().setScale(2));
        assertNotNull(e.getDataAtualizacao());
    }

    @Test
    void calculateSaldoDay_handlesNullsGracefully() throws Exception {
        ExtratoFinanceiro e = new ExtratoFinanceiro(LocalDate.now());
        e.setTotalCreditosDolares(null);
        e.setTotalDebitosDolares(null);
        Method onCreate = ExtratoFinanceiro.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(e);

        assertTrue(e.getSaldoDiaDolares() == null || e.getSaldoDiaDolares().compareTo(BigDecimal.ZERO) >= 0);
    }
}
