package com.web.dev.painelOnline.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.dev.painelOnline.entities.CambioHistorico;
import com.web.dev.painelOnline.services.CambioHistoricoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CambioHistoricoController.class)
class CambioHistoricoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CambioHistoricoService cambioHistoricoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void salvarTaxaCambio_created() throws Exception {
        CambioHistorico in = new CambioHistorico(LocalDate.of(2025,1,1), BigDecimal.valueOf(5));
        CambioHistorico saved = new CambioHistorico(LocalDate.of(2025,1,1), BigDecimal.valueOf(5));
        saved.setId(1L);

        Mockito.when(cambioHistoricoService.salvarTaxaCambio(any(), any())).thenReturn(saved);

        mvc.perform(post("/api/cambio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void salvarTaxaCambio_badRequest_onException() throws Exception {
        CambioHistorico in = new CambioHistorico();
        Mockito.when(cambioHistoricoService.salvarTaxaCambio(any(), any()))
                .thenThrow(new RuntimeException("fail"));

        mvc.perform(post("/api/cambio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarTaxaPorData_found_and_notFound() throws Exception {
        LocalDate d = LocalDate.of(2025,2,2);
        CambioHistorico c = new CambioHistorico(d, BigDecimal.valueOf(4));
        Mockito.when(cambioHistoricoService.buscarTaxaPorData(d)).thenReturn(Optional.of(c));

        mvc.perform(get("/api/cambio/data/{data}", d))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taxaUsdBrl").exists());

        Mockito.when(cambioHistoricoService.buscarTaxaPorData(d)).thenReturn(Optional.empty());
        mvc.perform(get("/api/cambio/data/{data}", d))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarUltimaTaxa_found_and_notFound() throws Exception {
        CambioHistorico c = new CambioHistorico(LocalDate.of(2025,3,3), BigDecimal.valueOf(6));
        c.setId(10L);
        Mockito.when(cambioHistoricoService.buscarUltimaTaxa()).thenReturn(Optional.of(c));

        mvc.perform(get("/api/cambio/ultima"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));

        Mockito.when(cambioHistoricoService.buscarUltimaTaxa()).thenReturn(Optional.empty());
        mvc.perform(get("/api/cambio/ultima"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarTaxaMaisRecenteAteData_found_and_notFound() throws Exception {
        LocalDate d = LocalDate.of(2025,4,4);
        CambioHistorico c = new CambioHistorico(d.minusDays(1), BigDecimal.valueOf(4.5));
        Mockito.when(cambioHistoricoService.buscarTaxaMaisRecenteAteData(d)).thenReturn(Optional.of(c));

        mvc.perform(get("/api/cambio/ate-data/{data}", d))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taxaUsdBrl").exists());

        Mockito.when(cambioHistoricoService.buscarTaxaMaisRecenteAteData(d)).thenReturn(Optional.empty());
        mvc.perform(get("/api/cambio/ate-data/{data}", d))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarTaxasPorPeriodo_ok_and_invalidDate() throws Exception {
        LocalDate inicio = LocalDate.of(2025,5,1);
        LocalDate fim = LocalDate.of(2025,5,31);
        CambioHistorico c1 = new CambioHistorico(inicio, BigDecimal.valueOf(4));
        Mockito.when(cambioHistoricoService.buscarTaxasPorPeriodo(inicio, fim)).thenReturn(List.of(c1));

        mvc.perform(get("/api/cambio/periodo")
                        .param("dataInicio", inicio.toString())
                        .param("dataFim", fim.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].data").exists());

        mvc.perform(get("/api/cambio/periodo")
                        .param("dataInicio", "invalid-date")
                        .param("dataFim", fim.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarTaxasMes_ok() throws Exception {
        CambioHistorico c1 = new CambioHistorico(LocalDate.of(2025,6,1), BigDecimal.valueOf(4.2));
        Mockito.when(cambioHistoricoService.buscarTaxasMes(2025, 6)).thenReturn(List.of(c1));

        mvc.perform(get("/api/cambio/mes/{ano}/{mes}", 2025, 6))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taxaUsdBrl").exists());
    }

    @Test
    void excluirTaxa_success_and_notFound() throws Exception {
        Mockito.doNothing().when(cambioHistoricoService).excluirTaxa(1L);
        mvc.perform(delete("/api/cambio/{id}", 1L))
                .andExpect(status().isNoContent());

        Mockito.doThrow(new RuntimeException("not found")).when(cambioHistoricoService).excluirTaxa(2L);
        mvc.perform(delete("/api/cambio/{id}", 2L))
                .andExpect(status().isNotFound());
    }
}
