package com.web.dev.painelOnline.services;

import com.web.dev.painelOnline.entities.CambioHistorico;
import com.web.dev.painelOnline.repository.CambioHistoricoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CambioHistoricoServiceTest {

    @Mock
    private CambioHistoricoRepository cambioHistoricoRepository;

    @InjectMocks
    private CambioHistoricoService cambioHistoricoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void salvarTaxaCambio_createsWhenNotExist() {
        LocalDate d = LocalDate.of(2025,1,1);
        when(cambioHistoricoRepository.findByData(d)).thenReturn(Optional.empty());

        CambioHistorico saved = new CambioHistorico(d, BigDecimal.valueOf(5));
        when(cambioHistoricoRepository.save(any())).thenReturn(saved);

        CambioHistorico result = cambioHistoricoService.salvarTaxaCambio(d, BigDecimal.valueOf(5));
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(5), result.getTaxaUsdBrl());
        verify(cambioHistoricoRepository).save(any());
    }

    @Test
    void salvarTaxaCambio_updatesWhenExists() {
        LocalDate d = LocalDate.of(2025,2,2);
        CambioHistorico existing = new CambioHistorico(d, BigDecimal.valueOf(4));
        existing.setId(10L);

        when(cambioHistoricoRepository.findByData(d)).thenReturn(Optional.of(existing));
        when(cambioHistoricoRepository.save(existing)).thenReturn(existing);

        CambioHistorico result = cambioHistoricoService.salvarTaxaCambio(d, BigDecimal.valueOf(4.5));
        assertEquals(BigDecimal.valueOf(4.5), result.getTaxaUsdBrl());
        verify(cambioHistoricoRepository).save(existing);
    }

    @Test
    void buscarUltimaTaxa_returnsEmptyWhenNone() {
        when(cambioHistoricoRepository.findUltimaTaxa()).thenReturn(Collections.emptyList());
        assertTrue(cambioHistoricoService.buscarUltimaTaxa().isEmpty());
    }

    @Test
    void buscarTaxaMaisRecenteAteData_returnsFirst() {
        LocalDate d = LocalDate.of(2025,3,3);
        CambioHistorico c1 = new CambioHistorico(d.minusDays(1), BigDecimal.valueOf(3));
        when(cambioHistoricoRepository.findTaxaMaisRecenteAteData(d)).thenReturn(List.of(c1));
        Optional<CambioHistorico> opt = cambioHistoricoService.buscarTaxaMaisRecenteAteData(d);
        assertTrue(opt.isPresent());
        assertEquals(BigDecimal.valueOf(3), opt.get().getTaxaUsdBrl());
    }

    @Test
    void excluirTaxa_callsRepositoryDelete() {
        doNothing().when(cambioHistoricoRepository).deleteById(5L);
        cambioHistoricoService.excluirTaxa(5L);
        verify(cambioHistoricoRepository).deleteById(5L);
    }
}
