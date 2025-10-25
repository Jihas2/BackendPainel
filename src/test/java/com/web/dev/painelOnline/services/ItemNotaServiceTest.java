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

class ItemNotaServiceTest {

    @Mock
    private ItemNotaRepository itemNotaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ExtratoFinanceiroService extratoFinanceiroService;

    @InjectMocks
    private ItemNotaService itemNotaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarItem_validates_andSaves() {
        Transacao tx = new Transacao();
        tx.setId(100L);
        tx.setData(LocalDate.of(2025, 8, 8));

        ItemNota in = new ItemNota("Prod", 2, BigDecimal.valueOf(5), tx);

        when(transacaoRepository.findById(100L)).thenReturn(Optional.of(tx));
        when(itemNotaRepository.save(any())).thenAnswer(inv -> {
            ItemNota arg = inv.getArgument(0);
            arg.setId(77L);
            return arg;
        });

        ItemNota saved = itemNotaService.criarItem(in);

        assertEquals(77L, saved.getId());
        assertEquals(BigDecimal.valueOf(10).setScale(2), saved.getValorTotal().setScale(2));
        verify(extratoFinanceiroService).atualizarExtratoDia(tx.getData());
    }

    @Test
    void criarItem_missingFields_throws() {
        ItemNota bad = new ItemNota();
        bad.setQuantidade(0);
        bad.setValorUnitario(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> itemNotaService.criarItem(bad));
        assertTrue(ex.getMessage().contains("descricao") || ex.getMessage().contains("quantidade") || ex.getMessage().contains("valorUnitario"));
    }

    @Test
    void atualizarItem_notFound_throwsRuntime() {
        when(itemNotaRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> itemNotaService.atualizarItem(1L, new ItemNota()));
        assertTrue(ex.getMessage().contains("ItemNota não encontrada"));
    }

    @Test
    void excluirItem_success_andUpdatesExtrato() {
        Transacao tx = new Transacao();
        tx.setData(LocalDate.of(2025, 9, 9));

        ItemNota existent = new ItemNota("A", 1, BigDecimal.ONE, tx);
        existent.setId(3L);

        when(itemNotaRepository.findById(3L)).thenReturn(Optional.of(existent));
        doNothing().when(itemNotaRepository).deleteById(3L);

        itemNotaService.excluirItem(3L);

        verify(itemNotaRepository).deleteById(3L);
        verify(extratoFinanceiroService).atualizarExtratoDia(tx.getData());
    }

    @Test
    void excluirItem_notFound_throws() {
        when(itemNotaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> itemNotaService.excluirItem(99L));
    }

    @Test
    void atualizarItem_transacaoAlterada_updatesExtrato() {
        Transacao oldTx = new Transacao();
        oldTx.setData(LocalDate.of(2025, 1, 1));
        Transacao newTx = new Transacao();
        newTx.setId(2L);
        newTx.setData(LocalDate.of(2025, 2, 2));

        ItemNota existente = new ItemNota("Produto", 1, BigDecimal.TEN, oldTx);
        existente.setId(1L);

        ItemNota atualizado = new ItemNota();
        atualizado.setTransacao(newTx);

        when(itemNotaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(transacaoRepository.findById(2L)).thenReturn(Optional.of(newTx));
        when(itemNotaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ItemNota result = itemNotaService.atualizarItem(1L, atualizado);

        assertEquals(newTx, result.getTransacao());
        verify(extratoFinanceiroService).atualizarExtratoDia(oldTx.getData());
        verify(extratoFinanceiroService).atualizarExtratoDia(newTx.getData());
    }

    @Test
    void atualizarItem_semAlteracaoTransacao_onlyRecalculatesValorTotal() {
        Transacao tx = new Transacao();
        tx.setData(LocalDate.of(2025, 3, 3));

        ItemNota existente = new ItemNota("Produto", 2, BigDecimal.valueOf(5), tx);
        existente.setId(5L);

        ItemNota atualizado = new ItemNota();
        atualizado.setQuantidade(3); // altera quantidade

        when(itemNotaRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(itemNotaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ItemNota result = itemNotaService.atualizarItem(5L, atualizado);

        assertEquals(BigDecimal.valueOf(15).setScale(2), result.getValorTotal().setScale(2));
        verify(extratoFinanceiroService).atualizarExtratoDia(tx.getData());
    }

    @Test
    void buscarTodosItens_returnsList() {
        ItemNota item = new ItemNota();
        when(itemNotaRepository.findAll()).thenReturn(List.of(item));

        List<ItemNota> result = itemNotaService.buscarTodosItens();
        assertEquals(1, result.size());
    }

    @Test
    void buscarItensPorTransacao_returnsList() {
        ItemNota item = new ItemNota();
        when(itemNotaRepository.findByTransacaoId(10L)).thenReturn(List.of(item));

        List<ItemNota> result = itemNotaService.buscarItensPorTransacao(10L);
        assertEquals(1, result.size());
    }

    @Test
    void buscarItemPorId_returnsOptional() {
        ItemNota item = new ItemNota();
        when(itemNotaRepository.findById(99L)).thenReturn(Optional.of(item));

        Optional<ItemNota> result = itemNotaService.buscarItemPorId(99L);
        assertTrue(result.isPresent());
    }

    @Test
    void buscarPorDescricao_returnsList() {
        ItemNota item = new ItemNota();
        when(itemNotaRepository.findByDescricaoContaining("abc")).thenReturn(List.of(item));

        List<ItemNota> result = itemNotaService.buscarPorDescricao("abc");
        assertEquals(1, result.size());
    }
}
