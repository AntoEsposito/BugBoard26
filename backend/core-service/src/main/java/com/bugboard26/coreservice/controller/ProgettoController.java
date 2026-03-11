package com.bugboard26.coreservice.controller;

import com.bugboard26.coreservice.dto.response.ProgettoResponse;
import com.bugboard26.coreservice.service.ProgettoService;
import jakarta.servlet.http.HttpServletRequest;
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
}
