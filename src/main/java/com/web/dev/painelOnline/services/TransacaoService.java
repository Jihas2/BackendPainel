package com.web.dev.painelOnline.services;

import com.web.dev.painelOnline.entities.Transacao;
import com.web.dev.painelOnline.entities.ItemNota;
import com.web.dev.painelOnline.repository.TransacaoRepository;
import com.web.dev.painelOnline.repository.ItemNotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ItemNotaRepository itemNotaRepository;

    @Autowired
    private ExtratoFinanceiroService extratoFinanceiroService;

    // Cria nova transação simples
    public Transacao criarTransacao(Transacao transacao) {
        Transacao transacaoSalva = transacaoRepository.save(transacao);

        // Atualiza o extrato financeiro do dia
        extratoFinanceiroService.atualizarExtratoDia(transacao.getData());

        return transacaoSalva;
    }

    // Cria transação com itens
    public Transacao criarTransacaoComItens(Transacao transacao, List<ItemNota> itens) {
        // valida os itens
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ItemNota item : itens) {
            if (item.getValorUnitario() == null || item.getQuantidade() == null) {
                throw new IllegalArgumentException("Itens devem conter valorUnitario e quantidade.");
            }
            BigDecimal valorItem = item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
            item.setValorTotal(valorItem);
            valorTotal = valorTotal.add(valorItem);
        }

        transacao.setValorReais(valorTotal);
        // salva transacao antes para ter id
        Transacao transacaoSalva = transacaoRepository.save(transacao);

        // Associa itens a transação e salva
        for (ItemNota item : itens) {
            item.setTransacao(transacaoSalva);
            itemNotaRepository.save(item);
        }

        // Atualiza extrato financeiro do dia
        extratoFinanceiroService.atualizarExtratoDia(transacao.getData());

        return transacaoSalva;
    }

    // Atualiza transacao
    public Transacao atualizarTransacao(Long id, Transacao transacaoAtualizada) {
        Optional<Transacao> transacaoExistente = transacaoRepository.findById(id);

        if (transacaoExistente.isPresent()) {
            Transacao transacao = transacaoExistente.get();
            LocalDate dataAnterior = transacao.getData();

            // Atualiza campos
            transacao.setData(transacaoAtualizada.getData());
            transacao.setCaracteristica(transacaoAtualizada.getCaracteristica());
            transacao.setValorReais(transacaoAtualizada.getValorReais());
            transacao.setTaxaCambio(transacaoAtualizada.getTaxaCambio());
            transacao.setTipoTransacao(transacaoAtualizada.getTipoTransacao());
            transacao.setStatusPagamento(transacaoAtualizada.getStatusPagamento());
            transacao.setTipoPagamento(transacaoAtualizada.getTipoPagamento());

            Transacao transacaoSalva = transacaoRepository.save(transacao);

            // Atualiza o extrato da data anterior e nova data
            extratoFinanceiroService.atualizarExtratoDia(dataAnterior);
            if (!dataAnterior.equals(transacao.getData())) {
                extratoFinanceiroService.atualizarExtratoDia(transacao.getData());
            }

            return transacaoSalva;
        }

        throw new RuntimeException("Transação não encontrada com ID: " + id);
    }

    public void excluirTransacao(Long id) {
        Optional<Transacao> transacao = transacaoRepository.findById(id);

        if (transacao.isPresent()) {
            LocalDate data = transacao.get().getData();
            transacaoRepository.deleteById(id);

            // Atualiza extrato financeiro do dia
            extratoFinanceiroService.atualizarExtratoDia(data);
        } else {
            throw new RuntimeException("Transação não encontrada com ID: " + id);
        }
    }

    @Transactional(readOnly = true)
    public List<Transacao> buscarTodasTransacoes() {
        return transacaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Transacao> buscarTransacaoPorId(Long id) {
        return transacaoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Transacao buscarTransacaoComItens(Long id) {
        return transacaoRepository.findTransacaoComItens(id);
    }

    @Transactional(readOnly = true)
    public List<Transacao> buscarTransacoesPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return transacaoRepository.findByDataBetween(dataInicio, dataFim);
    }

    @Transactional(readOnly = true)
    public List<Transacao> buscarTransacoesMes(int ano, int mes) {
        return transacaoRepository.findTransacoesPorMes(ano, mes);
    }

    @Transactional(readOnly = true)
    public List<Transacao> buscarDebitosAPrazoMes(int ano, int mes) {
        return transacaoRepository.findDebitosAPrazoPorMes(ano, mes);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPagamentosMes(int ano, int mes) {
        return transacaoRepository.calcularTotalPagamentosMes(ano, mes);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalDebitosPrazoMes(int ano, int mes) {
        return transacaoRepository.calcularTotalDebitosPrazoMes(ano, mes);
    }
}
