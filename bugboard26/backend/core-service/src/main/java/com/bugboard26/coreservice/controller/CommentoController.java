package com.bugboard26.coreservice.controller;

import org.springframework.web.bind.annotation.*;

import com.bugboard26.coreservice.dto.request.CreaCommentoRequest;
import com.bugboard26.coreservice.dto.response.CommentoResponse;
import com.bugboard26.coreservice.service.CommentoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/issue/{idIssue}/commenti")
@RequiredArgsConstructor
public class CommentoController extends AbstractController
{
    private final CommentoService commentoService;

    @PostMapping
    public ResponseEntity<CommentoResponse> aggiungiCommento(
            @PathVariable Integer idIssue,
            @Valid @RequestBody CreaCommentoRequest body,
            HttpServletRequest request)
    {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentoService.aggiungiCommento(idIssue, body, estraiUtente(request)));
    }
}
