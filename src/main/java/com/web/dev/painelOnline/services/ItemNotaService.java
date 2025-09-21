package com.web.dev.painelOnline.services;

import com.web.dev.painelOnline.entities.ItemNota;
import com.web.dev.painelOnline.entities.Transacao;
import com.web.dev.painelOnline.repository.ItemNotaRepository;
import com.web.dev.painelOnline.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ItemNotaService {

    @Autowired
    private ItemNotaRepository itemNotaRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    // Cria item validando os dados e associando a transacao gerenciada
    public ItemNota criarItem(ItemNota item) {
        validarItemBasico(item);

        if (item.getTransacao() == null || item.getTransacao().getId() == null) {
            throw new IllegalArgumentException("Campo 'transacao.id' é obrigatório.");
        }

        Optional<Transacao> transacaoOpt = transacaoRepository.findById(item.getTransacao().getId());
        if (!transacaoOpt.isPresent()) {
            throw new IllegalArgumentException("Transacao não encontrada para id: " + item.getTransacao().getId());
        }

        // associar entidade gerenciada
        item.setTransacao(transacaoOpt.get());

        // garantir valorTotal calculado
        item.setValorTotal(calcularValorTotalItem(item));

        return itemNotaRepository.save(item);
    }

    public ItemNota atualizarItem(Long id, ItemNota item) {
        Optional<ItemNota> existingOpt = itemNotaRepository.findById(id);
        if (!existingOpt.isPresent()) {
            throw new RuntimeException("ItemNota não encontrada com id: " + id);
        }

        ItemNota existente = existingOpt.get();

        if (item.getDescricao() != null) existente.setDescricao(item.getDescricao());
        if (item.getQuantidade() != null) existente.setQuantidade(item.getQuantidade());
        if (item.getValorUnitario() != null) existente.setValorUnitario(item.getValorUnitario());

        if (item.getTransacao() != null && item.getTransacao().getId() != null) {
            Optional<Transacao> transacaoOpt = transacaoRepository.findById(item.getTransacao().getId());
            if (!transacaoOpt.isPresent()) {
                throw new IllegalArgumentException("Transacao não encontrada para id: " + item.getTransacao().getId());
            }
            existente.setTransacao(transacaoOpt.get());
        }

        // recalcula valorTotal
        existente.setValorTotal(calcularValorTotalItem(existente));

        return itemNotaRepository.save(existente);
    }

    public void excluirItem(Long id) {
        if (!itemNotaRepository.existsById(id)) {
            throw new RuntimeException("ItemNota não encontrada com id: " + id);
        }
        itemNotaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ItemNota> buscarItensPorTransacao(Long transacaoId) {
        return itemNotaRepository.findByTransacaoId(transacaoId);
    }

    @Transactional(readOnly = true)
    public Optional<ItemNota> buscarItemPorId(Long id) {
        return itemNotaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ItemNota> buscarPorDescricao(String descricao) {
        return itemNotaRepository.findByDescricaoContaining(descricao);
    }

    private void validarItemBasico(ItemNota item) {
        if (item.getDescricao() == null || item.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Campo 'descricao' é obrigatório.");
        }
        if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Campo 'quantidade' deve ser maior que zero.");
        }
        if (item.getValorUnitario() == null) {
            throw new IllegalArgumentException("Campo 'valorUnitario' é obrigatório.");
        }
    }

    private BigDecimal calcularValorTotalItem(ItemNota item) {
        if (item.getValorUnitario() == null || item.getQuantidade() == null) {
            return BigDecimal.ZERO;
        }
        return item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
    }
}
