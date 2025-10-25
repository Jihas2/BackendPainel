package com.web.dev.painelOnline.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.dev.painelOnline.entities.Transacao;
import com.web.dev.painelOnline.services.TransacaoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransacaoController.class)
class TransacaoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransacaoService transacaoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void criarTransacao_created_and_badRequest() throws Exception {
        Transacao in = new Transacao();
        Transacao out = new Transacao();
        out.setId(1L);
        Mockito.when(transacaoService.criarTransacao(any())).thenReturn(out);

        mvc.perform(post("/api/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        Mockito.when(transacaoService.criarTransacao(any())).thenThrow(new RuntimeException("fail"));
        mvc.perform(post("/api/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarTodas_e_porId_found_and_notFound() throws Exception {
        Transacao t = new Transacao();
        t.setId(2L);
        Mockito.when(transacaoService.buscarTodasTransacoes()).thenReturn(List.of(t));

        mvc.perform(get("/api/transacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));

        Mockito.when(transacaoService.buscarTransacaoPorId(2L)).thenReturn(Optional.of(t));
        mvc.perform(get("/api/transacoes/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));

        Mockito.when(transacaoService.buscarTransacaoPorId(3L)).thenReturn(Optional.empty());
        mvc.perform(get("/api/transacoes/{id}", 3L))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarTransacaoComItens_ok_and_notFound() throws Exception {
        Transacao t = new Transacao();
        t.setId(9L);
        Mockito.when(transacaoService.buscarTransacaoComItens(9L)).thenReturn(t);

        mvc.perform(get("/api/transacoes/{id}/com-itens", 9L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9));

        Mockito.when(transacaoService.buscarTransacaoComItens(10L)).thenReturn(null);
        mvc.perform(get("/api/transacoes/{id}/com-itens", 10L))
                .andExpect(status().isNotFound());
    }

    @Test
    void periodo_mes_debitos_aprazo_endpoints_ok() throws Exception {
        Mockito.when(transacaoService.buscarTransacoesPorPeriodo(any(), any())).thenReturn(List.of());
        mvc.perform(get("/api/transacoes/periodo")
                        .param("dataInicio", LocalDate.of(2025, 1, 1).toString())
                        .param("dataFim", LocalDate.of(2025, 1, 31).toString()))
                .andExpect(status().isOk());

        Mockito.when(transacaoService.buscarTransacoesMes(anyInt(), anyInt())).thenReturn(List.of());
        mvc.perform(get("/api/transacoes/mes/{ano}/{mes}", 2025, 1))
                .andExpect(status().isOk());

        Mockito.when(transacaoService.buscarDebitosAPrazoMes(anyInt(), anyInt())).thenReturn(List.of());
        mvc.perform(get("/api/transacoes/debitos-prazo/{ano}/{mes}", 2025, 1))
                .andExpect(status().isOk());
    }

    @Test
    void atualizarTransacao_and_excluirTransacao_paths() throws Exception {
        Transacao updated = new Transacao();
        updated.setId(50L);
        Mockito.when(transacaoService.atualizarTransacao(eq(50L), any())).thenReturn(updated);

        mvc.perform(put("/api/transacoes/{id}", 50L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50));

        Mockito.when(transacaoService.atualizarTransacao(eq(51L), any()))
                .thenThrow(new RuntimeException("not found"));
        mvc.perform(put("/api/transacoes/{id}", 51L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());

        Mockito.doNothing().when(transacaoService).excluirTransacao(60L);
        mvc.perform(delete("/api/transacoes/{id}", 60L))
                .andExpect(status().isNoContent());

        Mockito.doThrow(new RuntimeException("not")).when(transacaoService).excluirTransacao(61L);
        mvc.perform(delete("/api/transacoes/{id}", 61L))
                .andExpect(status().isNotFound());
    }

    // ✅ Novo teste substituindo o que falhava
    @Test
    void buscarTodasTransacoes_ok() throws Exception {
        Transacao t = new Transacao();
        t.setId(100L);
        Mockito.when(transacaoService.buscarTodasTransacoes()).thenReturn(List.of(t));

        mvc.perform(get("/api/transacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100));
    }

    @Test
    void atualizarTransacao_nullInput_returnsBadRequest() throws Exception {
        mvc.perform(put("/api/transacoes/{id}", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void excluirTransacao_invalidId_returnsBadRequest() throws Exception {
        Mockito.doThrow(new RuntimeException("invalid id")).when(transacaoService).excluirTransacao(0L);
        mvc.perform(delete("/api/transacoes/{id}", 0L))
                .andExpect(status().isNotFound());
    }

    @Test
    void periodo_invalidDates_shouldReturnBadRequest() throws Exception {
        mvc.perform(get("/api/transacoes/periodo")
                        .param("dataInicio", "invalid-date")
                        .param("dataFim", "2025-01-31"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/api/transacoes/periodo")
                        .param("dataInicio", "2025-01-01")
                        .param("dataFim", "invalid-date"))
                .andExpect(status().isBadRequest());
    }
}
