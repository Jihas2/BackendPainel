package com.web.dev.painelOnline.services;

import com.web.dev.painelOnline.entities.ItemNota;
import com.web.dev.painelOnline.entities.Transacao;
import com.web.dev.painelOnline.repository.ItemNotaRepository;
import com.web.dev.painelOnline.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ItemNotaRepository itemNotaRepository;

    @Mock
    private ExtratoFinanceiroService extratoFinanceiroService;

    @InjectMocks
    private TransacaoService transacaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarTransacao_savesAndUpdatesExtrato() {
        Transacao t = new Transacao();
        t.setData(LocalDate.of(2025,10,10));

        when(transacaoRepository.save(t)).thenAnswer(inv -> {
            Transacao arg = inv.getArgument(0);
            arg.setId(55L);
            return arg;
        });

        Transacao saved = transacaoService.criarTransacao(t);
        assertEquals(55L, saved.getId());
        verify(transacaoRepository).save(t);
        verify(extratoFinanceiroService).atualizarExtratoDia(t.getData());
    }

    @Test
    void criarTransacao_repositoryThrows_exceptionPropagates() {
        Transacao t = new Transacao();
        t.setData(LocalDate.of(2025, 3, 3));
        when(transacaoRepository.save(any())).thenThrow(new RuntimeException("db fail"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> transacaoService.criarTransacao(t));
        assertEquals("db fail", ex.getMessage());
        verify(extratoFinanceiroService, never()).atualizarExtratoDia(any());
    }

    @Test
    void criarTransacaoComItens_validatesAndSaves() {
        Transacao t = new Transacao();
        t.setData(LocalDate.of(2025,11,11));

        ItemNota i1 = new ItemNota("X", 2, BigDecimal.valueOf(5), null);
        ItemNota i2 = new ItemNota("Y", 1, BigDecimal.valueOf(3), null);

        when(transacaoRepository.save(any())).thenAnswer(inv -> {
            Transacao arg = inv.getArgument(0);
            arg.setId(10L);
            return arg;
        });

        when(itemNotaRepository.save(any())).thenAnswer(inv -> {
            ItemNota arg = inv.getArgument(0);
            arg.setId(100L);
            return arg;
        });

        Transacao saved = transacaoService.criarTransacaoComItens(t, List.of(i1, i2));
        assertNotNull(saved.getId());
        assertEquals(BigDecimal.valueOf(13).setScale(0), saved.getValorReais().setScale(0));
        verify(itemNotaRepository, times(2)).save(any());
        verify(extratoFinanceiroService).atualizarExtratoDia(t.getData());
    }

    @Test
    void criarTransacaoComItens_emptyList_setsZeroAndSaves() {
        Transacao t = new Transacao();
        t.setData(LocalDate.of(2026,1,1));

        when(transacaoRepository.save(any())).thenAnswer(inv -> {
            Transacao arg = inv.getArgument(0);
            arg.setId(999L);
            return arg;
        });

        Transacao saved = transacaoService.criarTransacaoComItens(t, List.of());
        assertEquals(0, saved.getValorReais().compareTo(BigDecimal.ZERO));
        assertEquals(999L, saved.getId());
        verify(itemNotaRepository, never()).save(any());
        verify(extratoFinanceiroService).atualizarExtratoDia(t.getData());
    }

    @Test
    void criarTransacaoComItens_missingItemFields_throws() {
        Transacao t = new Transacao();
        ItemNota bad = new ItemNota("X", null, null, null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> transacaoService.criarTransacaoComItens(t, List.of(bad)));
        assertTrue(ex.getMessage().contains("Itens devem conter"));
    }

    @Test
    void atualizarTransacao_existing_updatesAndCallsExtrato_forDifferentDates() {
        Transacao persisted = new Transacao();
        persisted.setId(7L);
        persisted.setData(LocalDate.of(2025,1,1));

        Transacao updated = new Transacao();
        updated.setData(LocalDate.of(2025,2,2));
        updated.setCaracteristica("novo");

        when(transacaoRepository.findById(7L)).thenReturn(Optional.of(persisted));
        when(transacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Transacao res = transacaoService.atualizarTransacao(7L, updated);
        assertEquals("novo", res.getCaracteristica());
        verify(extratoFinanceiroService).atualizarExtratoDia(LocalDate.of(2025,1,1));
        verify(extratoFinanceiroService).atualizarExtratoDia(LocalDate.of(2025,2,2));
    }

    @Test
    void atualizarTransacao_existing_sameDate_callsExtratoOnlyOnce() {
        Transacao persisted = new Transacao();
        persisted.setId(8L);
        LocalDate same = LocalDate.of(2025,5,5);
        persisted.setData(same);

        Transacao updated = new Transacao();
        updated.setData(same);
        updated.setCaracteristica("igual");

        when(transacaoRepository.findById(8L)).thenReturn(Optional.of(persisted));
        when(transacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        transacaoService.atualizarTransacao(8L, updated);
        verify(extratoFinanceiroService, times(1)).atualizarExtratoDia(same);
    }

    @Test
    void atualizarTransacao_notFound_throws() {
        when(transacaoRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> transacaoService.atualizarTransacao(999L, new Transacao()));
    }

    @Test
    void excluirTransacao_existing_deletesAndUpdates() {
        Transacao t = new Transacao();
        t.setId(30L);
        t.setData(LocalDate.of(2025,12,12));
        when(transacaoRepository.findById(30L)).thenReturn(Optional.of(t));
        doNothing().when(transacaoRepository).deleteById(30L);

        transacaoService.excluirTransacao(30L);
        verify(transacaoRepository).deleteById(30L);
        verify(extratoFinanceiroService).atualizarExtratoDia(t.getData());
    }

    @Test
    void excluirTransacao_notFound_throws() {
        when(transacaoRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> transacaoService.excluirTransacao(123L));
    }

    @Test
    void buscarTodasTransacoes_delegatesToRepository() {
        Transacao t = new Transacao();
        when(transacaoRepository.findAll()).thenReturn(List.of(t));
        List<Transacao> result = transacaoService.buscarTodasTransacoes();
        assertEquals(1, result.size());
        verify(transacaoRepository).findAll();
    }

    @Test
    void buscarTransacaoPorId_delegatesToRepository() {
        Transacao t = new Transacao();
        t.setId(44L);
        when(transacaoRepository.findById(44L)).thenReturn(Optional.of(t));
        Optional<Transacao> opt = transacaoService.buscarTransacaoPorId(44L);
        assertTrue(opt.isPresent());
        assertEquals(44L, opt.get().getId());
    }

    @Test
    void buscarTransacaoComItens_returnsNullOrEntity() {

        Transacao t = new Transacao();
        t.setId(99L);
        when(transacaoRepository.findTransacaoComItens(99L)).thenReturn(t);
        Transacao found = transacaoService.buscarTransacaoComItens(99L);
        assertNotNull(found);

        when(transacaoRepository.findTransacaoComItens(100L)).thenReturn(null);
        Transacao notFound = transacaoService.buscarTransacaoComItens(100L);
        assertNull(notFound);
    }

    @Test
    void buscarTransacoesPorPeriodo_and_mes_and_debitosPrazo_delegations() {
        LocalDate start = LocalDate.of(2025,1,1);
        LocalDate end = LocalDate.of(2025,1,2);

        when(transacaoRepository.findByDataBetween(start, end)).thenReturn(List.of());
        when(transacaoRepository.findTransacoesPorMes(2025, 1)).thenReturn(List.of());
        when(transacaoRepository.findDebitosAPrazoPorMes(2025, 1)).thenReturn(List.of());

        assertEquals(0, transacaoService.buscarTransacoesPorPeriodo(start, end).size());
        assertEquals(0, transacaoService.buscarTransacoesMes(2025, 1).size());
        assertEquals(0, transacaoService.buscarDebitosAPrazoMes(2025, 1).size());

        verify(transacaoRepository).findByDataBetween(start, end);
        verify(transacaoRepository).findTransacoesPorMes(2025, 1);
        verify(transacaoRepository).findDebitosAPrazoPorMes(2025, 1);
    }

    @Test
    void calcularTotaisMes_delegates() {
        when(transacaoRepository.calcularTotalPagamentosMes(2025, 6)).thenReturn(BigDecimal.valueOf(200));
        when(transacaoRepository.calcularTotalDebitosPrazoMes(2025, 6)).thenReturn(BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(200), transacaoService.calcularTotalPagamentosMes(2025, 6));
        assertEquals(BigDecimal.valueOf(50), transacaoService.calcularTotalDebitosPrazoMes(2025, 6));
        verify(transacaoRepository).calcularTotalPagamentosMes(2025, 6);
        verify(transacaoRepository).calcularTotalDebitosPrazoMes(2025, 6);
    }
}
