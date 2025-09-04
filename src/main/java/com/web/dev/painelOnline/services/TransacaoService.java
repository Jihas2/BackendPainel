package com.web.dev.painelOnline.services;

import com.web.dev.painelOnline.entities.Transacao;
import com.web.dev.painelOnline.entities.ItemNota;
import com.web.dev.painelOnline.Enum.TipoTransacao;
import com.web.dev.painelOnline.Enum.StatusPagamento;
import com.web.dev.painelOnline.Enum.TipoPagamento;
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

    // Criar nova transação
    public Transacao criarTransacao(Transacao transacao) {
        Transacao transacaoSalva = transacaoRepository.save(transacao);

        // Atualizar extrato financeiro do dia
        extratoFinanceiroService.atualizarExtratoDia(transacao.getData());

        return transacaoSalva;
    }

    // Criar transação com itens (para débitos à prazo)
    public Transacao criarTransacaoComItens(Transacao transacao, List<ItemNota> itens) {
        // Calcular valor total dos itens
        BigDecimal valorTotal = itens.stream()
                .map(ItemNota::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        transacao.setValorReais(valorTotal);
        Transacao transacaoSalva = transacaoRepository.save(transacao);

        // Associar itens à transação e salvar
        itens.forEach(item -> {
            item.setTransacao(transacaoSalva);
            itemNotaRepository.save(item);
        });

        // Atualizar extrato financeiro do dia
        extratoFinanceiroService.atualizarExtratoDia(transacao.getData());

        return transacaoSalva;
    }

    // Atualizar transação
    public Transacao atualizarTransacao(Long id, Transacao transacaoAtualizada) {
        Optional<Transacao> transacaoExistente = transacaoRepository.findById(id);

        if (transacaoExistente.isPresent()) {
            Transacao transacao = transacaoExistente.get();
            LocalDate dataAnterior = transacao.getData();

            // Atualizar campos
            transacao.setData(transacaoAtualizada.getData());
            transacao.setCaracteristica(transacaoAtualizada.getCaracteristica());
            transacao.setValorReais(transacaoAtualizada.getValorReais());
            transacao.setTaxaCambio(transacaoAtualizada.getTaxaCambio());
            transacao.setTipoTransacao(transacaoAtualizada.getTipoTransacao());
            transacao.setStatusPagamento(transacaoAtualizada.getStatusPagamento());
            transacao.setTipoPagamento(transacaoAtualizada.getTipoPagamento());

            Transacao transacaoSalva = transacaoRepository.save(transacao);

            // Atualizar extrato da data anterior e nova data
            extratoFinanceiroService.atualizarExtratoDia(dataAnterior);
            if (!dataAnterior.equals(transacao.getData())) {
                extratoFinanceiroService.atualizarExtratoDia(transacao.getData());
            }

            return transacaoSalva;
        }

        throw new RuntimeException("Transação não encontrada com ID: " + id);
    }

    // Excluir transação
    public void excluirTransacao(Long id) {
        Optional<Transacao> transacao = transacaoRepository.findById(id);

        if (transacao.isPresent()) {
            LocalDate data = transacao.get().getData();
            transacaoRepository.deleteById(id);

            // Atualizar extrato financeiro do dia
            extratoFinanceiroService.atualizarExtratoDia(data);
        } else {
            throw new RuntimeException("Transação não encontrada com ID: " + id);
        }
    }

    // Buscar todas as transações
    @Transactional(readOnly = true)
    public List<Transacao> buscarTodasTransacoes() {
        return transacaoRepository.findAll();
    }

    // Buscar transação por ID
    @Transactional(readOnly = true)
    public Optional<Transacao> buscarTransacaoPorId(Long id) {
        return transacaoRepository.findById(id);
    }

    // Buscar transação com itens
    @Transactional(readOnly = true)
    public Transacao buscarTransacaoComItens(Long id) {
        return transacaoRepository.findTransacaoComItens(id);
    }

    // Buscar transações por período
    @Transactional(readOnly = true)
    public List<Transacao> buscarTransacoesPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return transacaoRepository.findByDataBetween(dataInicio, dataFim);
    }

    // Buscar transações do mês
    @Transactional(readOnly = true)
    public List<Transacao> buscarTransacoesMes(int ano, int mes) {
        return transacaoRepository.findTransacoesPorMes(ano, mes);
    }

    // Buscar débitos à prazo do mês
    @Transactional(readOnly = true)
    public List<Transacao> buscarDebitosAPrazoMes(int ano, int mes) {
        return transacaoRepository.findDebitosAPrazoPorMes(ano, mes);
    }

    // Calcular totais para dashboard
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPagamentosMes(int ano, int mes) {
        return transacaoRepository.calcularTotalPagamentosMes(ano, mes);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalDebitosPrazoMes(int ano, int mes) {
        return transacaoRepository.calcularTotalDebitosPrazoMes(ano, mes);
    }
}
