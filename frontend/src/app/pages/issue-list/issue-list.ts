import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProgettoResponse, IssueRiepilogoResponse } from '../../models/api.models';
import { ProgettoService } from '../../services/progetto.service';
import { IssueService } from '../../services/issue.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-issue-list',
  imports: [FormsModule, CommonModule],
  templateUrl: './issue-list.html',
  styleUrl: './issue-list.css',
})
export class IssueList implements OnInit {
  avatarNonCaricato = false;
  testoRicerca = '';

  ordinamento = 'data_creazione';
  mostraFiltri = false;
  filtroTipo = '';
  filtroPriorita = '';
  filtroStato = '';

  progetti: ProgettoResponse[] = [];
  progettoSelezionato: ProgettoResponse | null = null;
  issueList: IssueRiepilogoResponse[] = [];

  constructor(
    private readonly router: Router,
    private readonly progettoService: ProgettoService,
    private readonly issueService: IssueService,
    protected readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    this.progettoService.ottieniProgetti().subscribe(progetti => {
      this.progetti = progetti;
      if (progetti.length > 0) {
        this.progettoSelezionato = progetti[0];
        this.caricaIssue();
      }
    });
  }

  caricaIssue(): void {
    if (this.progettoSelezionato !== null) {
      this.issueService.ottieniIssuePerProgetto(this.progettoSelezionato.id).subscribe(issues => {
        this.issueList = issues;
      });
    }
  }

  selezionaProgetto(progetto: ProgettoResponse): void {
    this.progettoSelezionato = progetto;
    this.caricaIssue();
  }

  onNuovaIssue() {
    this.router.navigate(['/issue/nuova']);
  }

  onApriIssue(issue: IssueRiepilogoResponse) {
    this.router.navigate(['/issue', issue.id]);
  }

  onEsci() {
    this.router.navigate(['/login']);
  }

  mostraNascondiFiltri() {
    this.mostraFiltri = !this.mostraFiltri;
  }

  toggleFiltri() {
    this.mostraFiltri = !this.mostraFiltri;
  }

  setFiltroTipo(tipo: string) {
    this.filtroTipo = this.filtroTipo === tipo ? '' : tipo;
  }

  setFiltroPriorita(priorita: string) {
    this.filtroPriorita = this.filtroPriorita === priorita ? '' : priorita;
  }

  setFiltroStato(stato: string) {
    this.filtroStato = this.filtroStato === stato ? '' : stato;
  }

  getIconaTipo(tipo: string): string {
    switch (tipo) {
      case 'bug': return '🐛';
      case 'domanda': return '❓';
      case 'documentazione': return '📄';
      case 'funzionalita': return '✨';
      default: return '📋';
    }
  }
}
