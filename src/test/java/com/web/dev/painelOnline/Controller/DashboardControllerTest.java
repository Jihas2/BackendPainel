package com.web.dev.painelOnline.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.dev.painelOnline.services.DashboardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void obterDashboardAtual_ok() throws Exception {
        Mockito.when(dashboardService.obterDadosDashboard()).thenReturn(Map.of("key","value"));

        mvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("value"));
    }

    @Test
    void obterDashboardMesAno_ok() throws Exception {
        Mockito.when(dashboardService.obterDadosDashboardMes(anyInt(), anyInt())).thenReturn(Map.of("mes","ok"));
        Mockito.when(dashboardService.obterDadosDashboardAno(anyInt())).thenReturn(Map.of("ano","ok"));

        mvc.perform(get("/api/dashboard/mes/{ano}/{mes}", 2025, 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mes").value("ok"));

        mvc.perform(get("/api/dashboard/ano/{ano}", 2025))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ano").value("ok"));
    }
}
