package com.web.dev.painelOnline.Controller;

import com.web.dev.painelOnline.entities.ItemNota;
import com.web.dev.painelOnline.repository.ItemNotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/itens-nota")
@CrossOrigin(origins = "*")
public class ItemNotaController {

    @Autowired
    private ItemNotaRepository itemNotaRepository;

    // Buscar todos os itens de uma transação
    @GetMapping("/transacao/{transacaoId}")
    public ResponseEntity<List<ItemNota>> buscarItensPorTransacao(@PathVariable Long transacaoId) {
        List<ItemNota> itens = itemNotaRepository.findByTransacaoId(transacaoId);
        return ResponseEntity.ok(itens);
    }

    // Buscar item por ID
    @GetMapping("/{id}")
    public ResponseEntity<ItemNota> buscarItemPorId(@PathVariable Long id) {
        Optional<ItemNota> item = itemNotaRepository.findById(id);
        return item.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Criar novo item
    @PostMapping
    public ResponseEntity<ItemNota> criarItem(@RequestBody ItemNota item) {
        try {
            ItemNota novoItem = itemNotaRepository.save(item);
            return ResponseEntity.ok(novoItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Atualizar item
    @PutMapping("/{id}")
    public ResponseEntity<ItemNota> atualizarItem(@PathVariable Long id, @RequestBody ItemNota item) {
        if (itemNotaRepository.existsById(id)) {
            item.setId(id);
            ItemNota itemAtualizado = itemNotaRepository.save(item);
            return ResponseEntity.ok(itemAtualizado);
        }
        return ResponseEntity.notFound().build();
    }

    // Excluir item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirItem(@PathVariable Long id) {
        if (itemNotaRepository.existsById(id)) {
            itemNotaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Buscar itens por descrição
    @GetMapping("/buscar")
    public ResponseEntity<List<ItemNota>> buscarItensPorDescricao(@RequestParam String descricao) {
        List<ItemNota> itens = itemNotaRepository.findByDescricaoContaining(descricao);
        return ResponseEntity.ok(itens);
    }
}