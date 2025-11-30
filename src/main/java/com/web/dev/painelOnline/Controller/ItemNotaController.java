package com.web.dev.painelOnline.Controller;

import com.web.dev.painelOnline.dto.ItemNotaDTO;
import com.web.dev.painelOnline.entities.ItemNota;
import com.web.dev.painelOnline.services.ItemNotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/itens-nota")
@CrossOrigin(origins = "*")
public class ItemNotaController {

    @Autowired
    private ItemNotaService itemNotaService;

    @GetMapping("/transacao/{transacaoId}")
    public ResponseEntity<List<ItemNotaDTO>> buscarItensPorTransacao(@PathVariable Long transacaoId) {
        List<ItemNota> itens = itemNotaService.buscarItensPorTransacao(transacaoId);
        List<ItemNotaDTO> dtos = itens.stream()
                .map(item -> new ItemNotaDTO(
                        item.getId(),
                        item.getDescricao(),
                        item.getQuantidade(),
                        item.getValorUnitario(),
                        item.getValorTotal(),
                        item.getTransacao() != null ? item.getTransacao().getId() : null
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<ItemNotaDTO>> buscarTodosItens() {
        List<ItemNota> itens = itemNotaService.buscarTodosItens();
        List<ItemNotaDTO> dtos = itens.stream()
                .map(item -> new ItemNotaDTO(
                        item.getId(),
                        item.getDescricao(),
                        item.getQuantidade(),
                        item.getValorUnitario(),
                        item.getValorTotal(),
                        item.getTransacao() != null ? item.getTransacao().getId() : null
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarItemPorId(@PathVariable Long id) {
        return itemNotaService.buscarItemPorId(id)
                .map(item -> {
                    Map<String,Object> resp = new LinkedHashMap<>();
                    resp.put("id", item.getId());
                    resp.put("descricao", item.getDescricao());
                    resp.put("quantidade", item.getQuantidade());
                    resp.put("valorUnitario", item.getValorUnitario());
                    resp.put("valorTotal", item.getValorTotal());
                    resp.put("transacaoId", item.getTransacao() != null ? item.getTransacao().getId() : null);
                    return ResponseEntity.ok(resp);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criarItem(@RequestBody ItemNota item) {
        try {
            ItemNota salvo = itemNotaService.criarItem(item);
            Map<String,Object> resp = new LinkedHashMap<>();
            resp.put("id", salvo.getId());
            resp.put("descricao", salvo.getDescricao());
            resp.put("quantidade", salvo.getQuantidade());
            resp.put("valorUnitario", salvo.getValorUnitario());
            resp.put("valorTotal", salvo.getValorTotal());
            resp.put("transacaoId", salvo.getTransacao() != null ? salvo.getTransacao().getId() : null);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar ItemNota: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarItem(@PathVariable Long id, @RequestBody ItemNota item) {
        try {
            ItemNota atualizado = itemNotaService.atualizarItem(id, item);
            Map<String,Object> resp = new LinkedHashMap<>();
            resp.put("id", atualizado.getId());
            resp.put("descricao", atualizado.getDescricao());
            resp.put("quantidade", atualizado.getQuantidade());
            resp.put("valorUnitario", atualizado.getValorUnitario());
            resp.put("valorTotal", atualizado.getValorTotal());
            resp.put("transacaoId", atualizado.getTransacao() != null ? atualizado.getTransacao().getId() : null);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar ItemNota: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirItem(@PathVariable Long id) {
        try {
            itemNotaService.excluirItem(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ItemNotaDTO>> buscarItensPorDescricao(@RequestParam String descricao) {
        List<ItemNota> itens = itemNotaService.buscarPorDescricao(descricao);
        List<ItemNotaDTO> dtos = itens.stream()
                .map(item -> new ItemNotaDTO(
                        item.getId(),
                        item.getDescricao(),
                        item.getQuantidade(),
                        item.getValorUnitario(),
                        item.getValorTotal(),
                        item.getTransacao() != null ? item.getTransacao().getId() : null
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}