package com.web.dev.painelOnline.entities;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CambioHistoricoTest {

    @Test
    void onCreate_setsDataCriacao() throws Exception {
        CambioHistorico c = new CambioHistorico(LocalDate.now(), BigDecimal.valueOf(5.0));

        Method onCreate = CambioHistorico.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(c);

        assertNotNull(c.getDataCriacao());
        assertTrue(c.getDataCriacao() instanceof LocalDateTime);
    }

    @Test
    void gettersAndSetters_work() {
        CambioHistorico c = new CambioHistorico();
        c.setData(LocalDate.of(2025, 1, 1));
        c.setTaxaUsdBrl(BigDecimal.valueOf(4.5));
        assertEquals(LocalDate.of(2025, 1, 1), c.getData());
        assertEquals(BigDecimal.valueOf(4.5), c.getTaxaUsdBrl());
    }
}
