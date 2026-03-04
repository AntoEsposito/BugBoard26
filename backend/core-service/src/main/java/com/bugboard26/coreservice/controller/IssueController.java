package com.bugboard26.coreservice.controller;

import org.springframework.web.bind.annotation.*;

import com.bugboard26.coreservice.dto.request.CreaIssueRequest;
import com.bugboard26.coreservice.dto.request.ModificaIssueRequest;
import com.bugboard26.coreservice.dto.response.IssueDettaglioResponse;
import com.bugboard26.coreservice.dto.response.IssueRiepilogoResponse;
import com.bugboard26.coreservice.service.IssueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/issue")
@RequiredArgsConstructor
public class IssueController extends AbstractController
{
    private final IssueService issueService;

    @GetMapping
    public ResponseEntity<List<IssueRiepilogoResponse>> ottieniIssuePerProgetto(
            @RequestParam Integer idProgetto,
            HttpServletRequest request)
    {
        return ResponseEntity.ok(issueService.ottieniIssuePerProgetto(idProgetto, estraiUtente(request)));
    }

    @PostMapping
    public ResponseEntity<IssueRiepilogoResponse> creaIssue(
            @Valid @RequestBody CreaIssueRequest body,
            HttpServletRequest request)
    {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(issueService.creaIssue(body, estraiUtente(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueDettaglioResponse> ottieniDettaglio(
            @PathVariable Integer id,
            HttpServletRequest request)
    {
        return ResponseEntity.ok(issueService.ottieniDettaglioIssue(id, estraiUtente(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssueRiepilogoResponse> modificaIssue(
            @PathVariable Integer id,
            @Valid @RequestBody ModificaIssueRequest body,
            HttpServletRequest request)
    {
        return ResponseEntity.ok(issueService.modificaIssue(id, body, estraiUtente(request)));
    }
}
