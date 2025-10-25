package com.web.dev.painelOnline.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.dev.painelOnline.entities.ExtratoFinanceiro;
import com.web.dev.painelOnline.services.ExtratoFinanceiroService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExtratoFinanceiroController.class)
class ExtratoFinanceiroControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ExtratoFinanceiroService extratoFinanceiroService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void buscarExtratoPorData_ok_and_notFound() throws Exception {
        LocalDate d = LocalDate.of(2025,3,3);
        ExtratoFinanceiro e = new ExtratoFinanceiro(d);
        Mockito.when(extratoFinanceiroService.buscarExtratoPorData(d)).thenReturn(Optional.of(e));

        mvc.perform(get("/api/extratos/data/{data}", d))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        Mockito.when(extratoFinanceiroService.buscarExtratoPorData(d)).thenReturn(Optional.empty());
        mvc.perform(get("/api/extratos/data/{data}", d))
                .andExpect(status().isNotFound());
    }

    @Test
    void resumoMensalAndAnual_ok() throws Exception {
        Mockito.when(extratoFinanceiroService.calcularTotalCreditosMes(anyInt(), anyInt())).thenReturn(BigDecimal.TEN);
        Mockito.when(extratoFinanceiroService.calcularTotalDebitosMes(anyInt(), anyInt())).thenReturn(BigDecimal.ONE);
        Mockito.when(extratoFinanceiroService.calcularSaldoMes(anyInt(), anyInt())).thenReturn(BigDecimal.valueOf(9));

        mvc.perform(get("/api/extratos/resumo/mes/{ano}/{mes}", 2025, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCreditos").exists())
                .andExpect(jsonPath("$.saldoMes").exists());

        Mockito.when(extratoFinanceiroService.calcularTotalCreditosAno(anyInt())).thenReturn(BigDecimal.TEN);
        Mockito.when(extratoFinanceiroService.calcularTotalDebitosAno(anyInt())).thenReturn(BigDecimal.ONE);
        Mockito.when(extratoFinanceiroService.calcularSaldoAno(anyInt())).thenReturn(BigDecimal.valueOf(9));

        mvc.perform(get("/api/extratos/resumo/ano/{ano}", 2025))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCreditos").exists());
    }

    @Test
    void atualizarExtratoDia_ok_and_badRequest() throws Exception {
        LocalDate d = LocalDate.of(2025,4,4);
        ExtratoFinanceiro e = new ExtratoFinanceiro(d);
        Mockito.when(extratoFinanceiroService.atualizarExtratoDia(d)).thenReturn(e);

        mvc.perform(post("/api/extratos/atualizar/{data}", d))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        Mockito.when(extratoFinanceiroService.atualizarExtratoDia(d)).thenThrow(new RuntimeException("err"));
        mvc.perform(post("/api/extratos/atualizar/{data}", d))
                .andExpect(status().isBadRequest());
    }

    @Test
    void regenerarExtratosPeriodo_ok_and_badRequest() throws Exception {
        LocalDate start = LocalDate.of(2025,1,1);
        LocalDate end = LocalDate.of(2025,1,31);

        Mockito.doNothing().when(extratoFinanceiroService).regenerarExtratosPeriodo(start, end);

        mvc.perform(post("/api/extratos/regenerar")
                        .param("dataInicio", start.toString())
                        .param("dataFim", end.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Extratos regenerados com sucesso"));

        Mockito.doThrow(new RuntimeException("fail")).when(extratoFinanceiroService).regenerarExtratosPeriodo(start, end);

        mvc.perform(post("/api/extratos/regenerar")
                        .param("dataInicio", start.toString())
                        .param("dataFim", end.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Erro ao regenerar extratos")));
    }

    @Test
    void calcularSaldoAcumulado_ok() throws Exception {
        LocalDate d = LocalDate.of(2025,5,5);
        Mockito.when(extratoFinanceiroService.calcularSaldoAcumuladoAteData(d)).thenReturn(BigDecimal.valueOf(42));

        mvc.perform(get("/api/extratos/saldo-acumulado/{data}", d))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoAcumulado").value(42));
    }
}
