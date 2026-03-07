import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { CreaIssueRequest } from '../../models/api.models';
import { IssueService } from '../../services/issue.service';

@Component({
  selector: 'app-issue-create',
  imports: [FormsModule, CommonModule],
  templateUrl: './issue-create.html',
  styleUrl: './issue-create.css',
})
export class IssueCreate implements OnInit {
  titolo: string = '';
  descrizione: string = '';
  tipo: string = '';
  priorita: string = '';
  idProgetto: number = 0;

  caricamento: boolean = false;
  errore: string = '';
  successo: boolean = false;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly issueService: IssueService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.queryParamMap.get('idProgetto');
    if (idParam !== null) {
      this.idProgetto = Number(idParam);
    }
  }

  onCreaIssue(): void {
    this.errore = '';

    if (this.titolo.trim() === '') {
      this.errore = 'Il titolo è obbligatorio.';
      return;
    }

    if (this.tipo === '') {
      this.errore = 'Il tipo è obbligatorio.';
      return;
    }

    const request: CreaIssueRequest = {
      idProgetto: this.idProgetto,
      titolo: this.titolo.trim(),
      tipo: this.tipo,
      descrizione: this.descrizione.trim() !== '' ? this.descrizione.trim() : undefined,
      priorita: this.priorita !== '' ? this.priorita : undefined,
    };

    this.caricamento = true;
    this.issueService.creaIssue(request).subscribe({
      next: () => {
        this.caricamento = false;
        this.successo = true;
        setTimeout(() => this.router.navigate(['/issue-list']), 1500);
      },
      error: () => {
        this.caricamento = false;
        this.errore = 'Errore durante la creazione dell\'issue. Riprova.';
      }
    });
  }

  onAnnulla(): void {
    this.router.navigate(['/issue-list']);
  }
}
