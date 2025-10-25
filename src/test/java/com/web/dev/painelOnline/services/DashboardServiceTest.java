package com.web.dev.painelOnline.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardServiceTest {

    @Mock
    private TransacaoService transacaoService;

    @Mock
    private ExtratoFinanceiroService extratoFinanceiroService;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obterDadosDashboardMes_populatesMap() {
        when(transacaoService.calcularTotalPagamentosMes(2025, 1)).thenReturn(BigDecimal.TEN);
        when(transacaoService.calcularTotalDebitosPrazoMes(2025, 1)).thenReturn(BigDecimal.valueOf(2));
        when(extratoFinanceiroService.calcularTotalCreditosMes(2025, 1)).thenReturn(BigDecimal.valueOf(3));
        when(extratoFinanceiroService.calcularTotalDebitosMes(2025, 1)).thenReturn(BigDecimal.valueOf(1));
        when(extratoFinanceiroService.calcularSaldoMes(2025, 1)).thenReturn(BigDecimal.valueOf(2));
        when(extratoFinanceiroService.calcularSaldoAcumuladoAteData(any())).thenReturn(BigDecimal.valueOf(20));

        Map<String, Object> map = dashboardService.obterDadosDashboardMes(2025, 1);
        assertEquals(BigDecimal.TEN, map.get("totalPagamentosMes"));
        assertEquals(2025, map.get("ano"));
        assertEquals(1, map.get("mes"));
        assertEquals(BigDecimal.valueOf(20), map.get("saldoAcumulado"));
    }

    @Test
    void obterDadosDashboardAno_populatesMap() {
        when(extratoFinanceiroService.calcularTotalCreditosAno(2024)).thenReturn(BigDecimal.valueOf(100));
        when(extratoFinanceiroService.calcularTotalDebitosAno(2024)).thenReturn(BigDecimal.valueOf(50));
        when(extratoFinanceiroService.calcularSaldoAno(2024)).thenReturn(BigDecimal.valueOf(50));
        when(extratoFinanceiroService.calcularSaldoAcumuladoAteData(any())).thenReturn(BigDecimal.valueOf(200));

        Map<String, Object> map = dashboardService.obterDadosDashboardAno(2024);
        assertEquals(BigDecimal.valueOf(100), map.get("totalCreditosAno"));
        assertEquals(2024, map.get("ano"));
        assertEquals(BigDecimal.valueOf(200), map.get("saldoAcumulado"));
    }

    @Test
    void obterDadosDashboard_callsMesWithNow() {

        when(transacaoService.calcularTotalPagamentosMes(anyInt(), anyInt())).thenReturn(BigDecimal.ZERO);
        when(transacaoService.calcularTotalDebitosPrazoMes(anyInt(), anyInt())).thenReturn(BigDecimal.ZERO);
        when(extratoFinanceiroService.calcularTotalCreditosMes(anyInt(), anyInt())).thenReturn(BigDecimal.ZERO);
        when(extratoFinanceiroService.calcularTotalDebitosMes(anyInt(), anyInt())).thenReturn(BigDecimal.ZERO);
        when(extratoFinanceiroService.calcularSaldoMes(anyInt(), anyInt())).thenReturn(BigDecimal.ZERO);
        when(extratoFinanceiroService.calcularSaldoAcumuladoAteData(any())).thenReturn(BigDecimal.ZERO);

        Map<String, Object> map = dashboardService.obterDadosDashboard();
        assertNotNull(map.get("ano"));
        assertNotNull(map.get("mes"));
    }
}
