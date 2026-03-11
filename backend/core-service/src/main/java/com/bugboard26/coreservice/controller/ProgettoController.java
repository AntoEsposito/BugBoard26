package com.bugboard26.coreservice.controller;

import com.bugboard26.coreservice.dto.request.ModificaMembriRequest;
import com.bugboard26.coreservice.dto.response.ProgettoResponse;
import com.bugboard26.coreservice.dto.response.UtenteResponse;
import com.bugboard26.coreservice.service.ProgettoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progetti")
@RequiredArgsConstructor
public class ProgettoController extends AbstractController
{
    private final ProgettoService progettoService;

    @GetMapping
    public ResponseEntity<List<ProgettoResponse>> ottieniProgetti(HttpServletRequest request)
    {
        return ResponseEntity.ok(progettoService.ottieniProgetti(estraiUtente(request)));
    }

    @GetMapping("/{id}/membri")
    public ResponseEntity<List<UtenteResponse>> ottieniMembri(@PathVariable Integer id, HttpServletRequest request)
    {
        return ResponseEntity.ok(progettoService.ottieniMembri(id, estraiUtente(request)));
    }

    @PostMapping("/{id}/membri")
    public ResponseEntity<List<UtenteResponse>> aggiungiMembri(
            @PathVariable Integer id,
            @Valid @RequestBody ModificaMembriRequest body,
            HttpServletRequest request)
    {
        return ResponseEntity.ok(progettoService.aggiungiMembri(id, body.getIdUtenti(), estraiUtente(request)));
    }

    @DeleteMapping("/{id}/membri")
    public ResponseEntity<Void> rimuoviMembri(
            @PathVariable Integer id,
            @Valid @RequestBody ModificaMembriRequest body,
            HttpServletRequest request)
    {
        progettoService.rimuoviMembri(id, body.getIdUtenti(), estraiUtente(request));
        return ResponseEntity.noContent().build();
    }
}
