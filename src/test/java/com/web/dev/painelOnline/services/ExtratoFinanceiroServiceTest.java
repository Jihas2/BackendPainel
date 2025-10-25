package com.web.dev.painelOnline.services;

import com.web.dev.painelOnline.entities.ExtratoFinanceiro;
import com.web.dev.painelOnline.repository.ExtratoFinanceiroRepository;
import com.web.dev.painelOnline.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExtratoFinanceiroServiceTest {

    @Mock
    private ExtratoFinanceiroRepository extratoFinanceiroRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private ExtratoFinanceiroService extratoFinanceiroService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void atualizarExtratoDia_createsWhenNotExists_andSaves() {
        LocalDate d = LocalDate.of(2025,6,6);

        when(extratoFinanceiroRepository.findByData(d)).thenReturn(Optional.empty());
        when(transacaoRepository.calcularTotalCreditosPorData(d)).thenReturn(BigDecimal.valueOf(100));
        when(transacaoRepository.calcularTotalDebitosPorData(d)).thenReturn(BigDecimal.valueOf(30));
        when(extratoFinanceiroRepository.calcularSaldoAcumuladoAteData(d.minusDays(1))).thenReturn(BigDecimal.ZERO);

        ExtratoFinanceiro saved = new ExtratoFinanceiro(d);
        saved.setId(1L);
        when(extratoFinanceiroRepository.save(any())).thenReturn(saved);

        ExtratoFinanceiro result = extratoFinanceiroService.atualizarExtratoDia(d);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(extratoFinanceiroRepository).save(any());
    }

    @Test
    void atualizarExtratoDia_updatesExistingExtrato() {
        LocalDate d = LocalDate.of(2025, 6, 7);
        ExtratoFinanceiro existing = new ExtratoFinanceiro(d);
        existing.setId(5L);
        existing.setTotalCreditosDolares(BigDecimal.ZERO);
        existing.setTotalDebitosDolares(BigDecimal.ZERO);

        when(extratoFinanceiroRepository.findByData(d)).thenReturn(Optional.of(existing));
        when(transacaoRepository.calcularTotalCreditosPorData(d)).thenReturn(BigDecimal.valueOf(50));
        when(transacaoRepository.calcularTotalDebitosPorData(d)).thenReturn(BigDecimal.valueOf(20));
        when(extratoFinanceiroRepository.calcularSaldoAcumuladoAteData(d.minusDays(1))).thenReturn(BigDecimal.valueOf(10));
        when(extratoFinanceiroRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ExtratoFinanceiro result = extratoFinanceiroService.atualizarExtratoDia(d);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(50), result.getTotalCreditosDolares());
        assertEquals(BigDecimal.valueOf(20), result.getTotalDebitosDolares());
        assertEquals(BigDecimal.valueOf(30), result.getSaldoDiaDolares());
        assertEquals(BigDecimal.valueOf(40), result.getSaldoAcumuladoDolares());
        verify(extratoFinanceiroRepository).save(any());
    }

    @Test
    void calcularSaldoAcumuladoAteData_delegatesToRepository() {
        LocalDate d = LocalDate.of(2025,7,7);
        when(extratoFinanceiroRepository.calcularSaldoAcumuladoAteData(d)).thenReturn(BigDecimal.valueOf(123));
        BigDecimal result = extratoFinanceiroService.calcularSaldoAcumuladoAteData(d);
        assertEquals(BigDecimal.valueOf(123), result);
        verify(extratoFinanceiroRepository).calcularSaldoAcumuladoAteData(d);
    }

    @Test
    void buscarExtratoPorData_presentAndEmpty() {
        LocalDate d = LocalDate.of(2025,8,8);
        ExtratoFinanceiro e = new ExtratoFinanceiro(d);
        when(extratoFinanceiroRepository.findByData(d)).thenReturn(Optional.of(e));

        Optional<ExtratoFinanceiro> found = extratoFinanceiroService.buscarExtratoPorData(d);
        assertTrue(found.isPresent());

        LocalDate other = LocalDate.of(2025,8,9);
        when(extratoFinanceiroRepository.findByData(other)).thenReturn(Optional.empty());
        Optional<ExtratoFinanceiro> notFound = extratoFinanceiroService.buscarExtratoPorData(other);
        assertTrue(notFound.isEmpty());
    }

    @Test
    void buscarExtratosPorPeriodo_returnsList() {
        LocalDate start = LocalDate.of(2025,1,1);
        LocalDate end = LocalDate.of(2025,1,5);
        ExtratoFinanceiro e = new ExtratoFinanceiro(start);
        when(extratoFinanceiroRepository.findByDataBetween(start, end)).thenReturn(List.of(e));

        List<ExtratoFinanceiro> list = extratoFinanceiroService.buscarExtratosPorPeriodo(start, end);
        assertEquals(1, list.size());
        verify(extratoFinanceiroRepository).findByDataBetween(start, end);
    }

    @Test
    void buscarExtratosMes_andAno_returnLists() {
        ExtratoFinanceiro e = new ExtratoFinanceiro(LocalDate.of(2025, 3, 10));
        when(extratoFinanceiroRepository.findExtratosPorMes(2025, 3)).thenReturn(List.of(e));
        when(extratoFinanceiroRepository.findExtratosPorAno(2025)).thenReturn(List.of(e));

        List<ExtratoFinanceiro> mes = extratoFinanceiroService.buscarExtratosMes(2025, 3);
        List<ExtratoFinanceiro> ano = extratoFinanceiroService.buscarExtratosAno(2025);

        assertEquals(1, mes.size());
        assertEquals(1, ano.size());
        verify(extratoFinanceiroRepository).findExtratosPorMes(2025, 3);
        verify(extratoFinanceiroRepository).findExtratosPorAno(2025);
    }

    @Test
    void calcularTotaisMesEAno_andSaldoMethods() {
        when(extratoFinanceiroRepository.calcularTotalCreditosMes(2025, 4)).thenReturn(BigDecimal.valueOf(1000));
        when(extratoFinanceiroRepository.calcularTotalDebitosMes(2025, 4)).thenReturn(BigDecimal.valueOf(400));
        when(extratoFinanceiroRepository.calcularSaldoMes(2025, 4)).thenReturn(BigDecimal.valueOf(600));

        when(extratoFinanceiroRepository.calcularTotalCreditosAno(2025)).thenReturn(BigDecimal.valueOf(5000));
        when(extratoFinanceiroRepository.calcularTotalDebitosAno(2025)).thenReturn(BigDecimal.valueOf(2000));
        when(extratoFinanceiroRepository.calcularSaldoAno(2025)).thenReturn(BigDecimal.valueOf(3000));

        assertEquals(BigDecimal.valueOf(1000), extratoFinanceiroService.calcularTotalCreditosMes(2025, 4));
        assertEquals(BigDecimal.valueOf(400), extratoFinanceiroService.calcularTotalDebitosMes(2025, 4));
        assertEquals(BigDecimal.valueOf(600), extratoFinanceiroService.calcularSaldoMes(2025, 4));

        assertEquals(BigDecimal.valueOf(5000), extratoFinanceiroService.calcularTotalCreditosAno(2025));
        assertEquals(BigDecimal.valueOf(2000), extratoFinanceiroService.calcularTotalDebitosAno(2025));
        assertEquals(BigDecimal.valueOf(3000), extratoFinanceiroService.calcularSaldoAno(2025));

        verify(extratoFinanceiroRepository).calcularTotalCreditosMes(2025, 4);
        verify(extratoFinanceiroRepository).calcularTotalDebitosMes(2025, 4);
        verify(extratoFinanceiroRepository).calcularSaldoMes(2025, 4);

        verify(extratoFinanceiroRepository).calcularTotalCreditosAno(2025);
        verify(extratoFinanceiroRepository).calcularTotalDebitosAno(2025);
        verify(extratoFinanceiroRepository).calcularSaldoAno(2025);
    }

    @Test
    void regenerarExtratosPeriodo_iteratesAndCallsUpdate() {
        LocalDate start = LocalDate.of(2025,1,1);
        LocalDate end = LocalDate.of(2025,1,3);

        ExtratoFinanceiro e = new ExtratoFinanceiro(start);

        when(extratoFinanceiroRepository.findByData(any())).thenReturn(Optional.of(e));
        when(transacaoRepository.calcularTotalCreditosPorData(any())).thenReturn(BigDecimal.ZERO);
        when(transacaoRepository.calcularTotalDebitosPorData(any())).thenReturn(BigDecimal.ZERO);
        when(extratoFinanceiroRepository.calcularSaldoAcumuladoAteData(any())).thenReturn(BigDecimal.ZERO);
        when(extratoFinanceiroRepository.save(any())).thenReturn(e);

        extratoFinanceiroService.regenerarExtratosPeriodo(start, end);

        verify(extratoFinanceiroRepository, atLeastOnce()).save(any());
        verify(extratoFinanceiroRepository, times(3)).findByData(any());
    }
}
