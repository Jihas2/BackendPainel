package com.web.dev.painelOnline.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.dev.painelOnline.entities.ItemNota;
import com.web.dev.painelOnline.services.ItemNotaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ItemNotaController.class)
class ItemNotaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemNotaService itemNotaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void buscarItensPorTransacao_ok() throws Exception {
        ItemNota i = new ItemNota("X",1,BigDecimal.TEN,null);
        Mockito.when(itemNotaService.buscarItensPorTransacao(5L)).thenReturn(List.of(i));

        mvc.perform(get("/api/itens-nota/transacao/{transacaoId}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descricao").value("X"));
    }

    @Test
    void buscarItemPorId_ok_and_notFound() throws Exception {
        ItemNota i = new ItemNota("X",2,BigDecimal.ONE,null);
        i.setId(7L);
        Mockito.when(itemNotaService.buscarItemPorId(7L)).thenReturn(Optional.of(i));
        mvc.perform(get("/api/itens-nota/{id}", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7));

        Mockito.when(itemNotaService.buscarItemPorId(8L)).thenReturn(Optional.empty());
        mvc.perform(get("/api/itens-nota/{id}", 8L))
                .andExpect(status().isNotFound());
    }

    @Test
    void criarItem_created_and_badRequest_and_internalServerError() throws Exception {
        ItemNota in = new ItemNota("Y",3,BigDecimal.valueOf(2), null);
        ItemNota saved = new ItemNota("Y",3,BigDecimal.valueOf(2), null);
        saved.setId(11L);

        Mockito.when(itemNotaService.criarItem(any())).thenReturn(saved);
        mvc.perform(post("/api/itens-nota")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(11));

        Mockito.reset(itemNotaService);

        Mockito.doThrow(new IllegalArgumentException("bad")).when(itemNotaService).criarItem(any());
        mvc.perform(post("/api/itens-nota")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("bad"));

        Mockito.reset(itemNotaService);

        Mockito.doThrow(new RuntimeException("fail")).when(itemNotaService).criarItem(any());
        mvc.perform(post("/api/itens-nota")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Erro ao criar ItemNota")));
    }

    @Test
    void atualizarItem_variousPaths() throws Exception {
        ItemNota in = new ItemNota("Z",1,BigDecimal.ONE,null);
        ItemNota updated = new ItemNota("Z",1,BigDecimal.ONE,null);
        updated.setId(20L);

        Mockito.when(itemNotaService.atualizarItem(eq(20L), any())).thenReturn(updated);
        mvc.perform(put("/api/itens-nota/{id}", 20L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20));

        Mockito.reset(itemNotaService);

        Mockito.doThrow(new IllegalArgumentException("bad")).when(itemNotaService).atualizarItem(eq(21L), any());
        mvc.perform(put("/api/itens-nota/{id}", 21L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isBadRequest());

        Mockito.reset(itemNotaService);

        Mockito.doThrow(new RuntimeException("not found")).when(itemNotaService).atualizarItem(eq(22L), any());
        mvc.perform(put("/api/itens-nota/{id}", 22L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound());
    }

    @Test
    void excluirItem_success_and_notFound() throws Exception {
        Mockito.doNothing().when(itemNotaService).excluirItem(30L);
        mvc.perform(delete("/api/itens-nota/{id}", 30L))
                .andExpect(status().isNoContent());

        Mockito.reset(itemNotaService);

        Mockito.doThrow(new RuntimeException("not")).when(itemNotaService).excluirItem(31L);
        mvc.perform(delete("/api/itens-nota/{id}", 31L))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorDescricao_ok() throws Exception {
        ItemNota i = new ItemNota("abc",1,BigDecimal.ONE,null);
        Mockito.when(itemNotaService.buscarPorDescricao("abc")).thenReturn(List.of(i));
        mvc.perform(get("/api/itens-nota/buscar").param("descricao","abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descricao").value("abc"));
    }
}
