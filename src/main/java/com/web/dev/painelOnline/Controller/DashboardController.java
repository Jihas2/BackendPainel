package com.web.dev.painelOnline.Controller;

import com.web.dev.painelOnline.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    // Dados do dashboard do mes atual
    @GetMapping
    public ResponseEntity<Map<String, Object>> obterDashboardAtual() {
        Map<String, Object> dashboard = dashboardService.obterDadosDashboard();
        return ResponseEntity.ok(dashboard);
    }

    // Dados do dashboard de um mes específico
    @GetMapping("/mes/{ano}/{mes}")
    public ResponseEntity<Map<String, Object>> obterDashboardMes(
            @PathVariable int ano,
            @PathVariable int mes) {
        Map<String, Object> dashboard = dashboardService.obterDadosDashboardMes(ano, mes);
        return ResponseEntity.ok(dashboard);
    }

    // Dados do dashboard de um ano específico
    @GetMapping("/ano/{ano}")
    public ResponseEntity<Map<String, Object>> obterDashboardAno(@PathVariable int ano) {
        Map<String, Object> dashboard = dashboardService.obterDadosDashboardAno(ano);
        return ResponseEntity.ok(dashboard);
    }
}
