package com.web.dev.painelOnline.Controller;

import com.web.dev.painelOnline.entities.Transacao;
import com.web.dev.painelOnline.entities.ItemNota;
import com.web.dev.painelOnline.services.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/transacoes")
@CrossOrigin(origins = "*")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    // Cria uma nova transação
    @PostMapping
    public ResponseEntity<Transacao> criarTransacao(@RequestBody Transacao transacao) {
        try {
            Transacao novaTransacao = transacaoService.criarTransacao(transacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaTransacao);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Cria uma transação com itens para débitos à prazo
    @PostMapping("/com-itens")
    public ResponseEntity<Transacao> criarTransacaoComItens(@RequestBody Map<String, Object> request) {
        try {

            Object transacaoObj = request.get("transacao");
            Object itensObj = request.get("itens");

            Transacao transacao = (Transacao) transacaoObj;
            @SuppressWarnings("unchecked")
            List<ItemNota> itens = (List<ItemNota>) itensObj;

            Transacao novaTransacao = transacaoService.criarTransacaoComItens(transacao, itens);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaTransacao);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Busca todas as transações
    @GetMapping
    public ResponseEntity<List<Transacao>> buscarTodasTransacoes() {
        List<Transacao> transacoes = transacaoService.buscarTodasTransacoes();
        return ResponseEntity.ok(transacoes);
    }

    // Busca a transação por ID
    @GetMapping("/{id}")
    public ResponseEntity<Transacao> buscarTransacaoPorId(@PathVariable Long id) {
        Optional<Transacao> transacao = transacaoService.buscarTransacaoPorId(id);
        return transacao.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Busca a transação com itens
    @GetMapping("/{id}/com-itens")
    public ResponseEntity<Transacao> buscarTransacaoComItens(@PathVariable Long id) {
        try {
            Transacao transacao = transacaoService.buscarTransacaoComItens(id);
            if (transacao != null) {
                return ResponseEntity.ok(transacao);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Busca as transações por período
    @GetMapping("/periodo")
    public ResponseEntity<List<Transacao>> buscarTransacoesPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<Transacao> transacoes = transacaoService.buscarTransacoesPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(transacoes);
    }

    // Busca as transações do mes
    @GetMapping("/mes/{ano}/{mes}")
    public ResponseEntity<List<Transacao>> buscarTransacoesMes(
            @PathVariable int ano,
            @PathVariable int mes) {
        List<Transacao> transacoes = transacaoService.buscarTransacoesMes(ano, mes);
        return ResponseEntity.ok(transacoes);
    }

    // Busca débitos à prazo do mes
    @GetMapping("/debitos-prazo/{ano}/{mes}")
    public ResponseEntity<List<Transacao>> buscarDebitosAPrazoMes(
            @PathVariable int ano,
            @PathVariable int mes) {
        List<Transacao> debitos = transacaoService.buscarDebitosAPrazoMes(ano, mes);
        return ResponseEntity.ok(debitos);
    }

    // Atualiza a transação
    @PutMapping("/{id}")
    public ResponseEntity<Transacao> atualizarTransacao(
            @PathVariable Long id,
            @RequestBody Transacao transacao) {
        try {
            Transacao transacaoAtualizada = transacaoService.atualizarTransacao(id, transacao);
            return ResponseEntity.ok(transacaoAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Atualiza a quantidade de itens de uma transação
    @PutMapping("/{id}/atualizar-quantidade-itens")
    public ResponseEntity<Transacao> atualizarQuantidadeItens(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer novaQuantidade = request.get("quantidade");
            if (novaQuantidade == null || novaQuantidade < 0) {
                return ResponseEntity.badRequest().build();
            }

            Transacao transacaoAtualizada = transacaoService.atualizarQuantidadeItens(id, novaQuantidade);
            return ResponseEntity.ok(transacaoAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // BUSCA POR CARACTERÍSTICA (DESCRIÇÃO)
    @GetMapping("/buscar")
    public ResponseEntity<List<Transacao>> buscarPorCaracteristica(
            @RequestParam String caracteristica) {

        List<Transacao> transacoes =
                transacaoService.buscarPorCaracteristica(caracteristica);

        return ResponseEntity.ok(transacoes);
    }

    // Exclui a  transação
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirTransacao(@PathVariable Long id) {
        try {
            transacaoService.excluirTransacao(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}