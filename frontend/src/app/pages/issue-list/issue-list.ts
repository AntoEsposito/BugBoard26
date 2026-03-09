import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { ProgettoResponse, IssueRiepilogoResponse } from '../../models/api.models';
import { ProgettoService } from '../../services/progetto.service';
import { IssueService } from '../../services/issue.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-issue-list',
  imports: [FormsModule, CommonModule, DatePipe],
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
  issueListCompleta: IssueRiepilogoResponse[] = [];
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
        this.issueListCompleta = issues;
        this.issueList = issues;
      });
    }
  }

  applicaFiltri(): void {
    let risultato = this.issueListCompleta;

    if (this.testoRicerca.trim() !== '') {
      const testo = this.testoRicerca.toLowerCase();
      risultato = risultato.filter(i =>
        i.titolo.toLowerCase().includes(testo) ||
        i.descrizione.toLowerCase().includes(testo)
      );
    }

    if (this.filtroTipo !== '') {
      risultato = risultato.filter(i => i.tipo === this.filtroTipo);
    }

    if (this.filtroPriorita !== '') {
      risultato = risultato.filter(i => i.priorita === this.filtroPriorita);
    }

    if (this.filtroStato !== '') {
      risultato = risultato.filter(i => i.stato === this.filtroStato);
    }

    risultato = this.ordinaIssue(risultato);

    this.issueList = risultato;
  }

  ordinaIssue(lista: IssueRiepilogoResponse[]): IssueRiepilogoResponse[] {
    const pesoPriorita: Record<string, number> = { 'HIGH': 3, 'MEDIUM': 2, 'LOW': 1 };
    const pesoStato: Record<string, number> = { 'TODO': 1, 'IN_PROGRESS': 2, 'DONE': 3 };

    return [...lista].sort((a, b) => {
      switch (this.ordinamento) {
        case 'priorita':
          return (pesoPriorita[b.priorita] ?? 0) - (pesoPriorita[a.priorita] ?? 0);
        case 'stato':
          return (pesoStato[a.stato] ?? 0) - (pesoStato[b.stato] ?? 0);
        case 'tipo':
          return a.tipo.localeCompare(b.tipo);
        case 'data_creazione':
        default:
          return new Date(b.dataCreazione).getTime() - new Date(a.dataCreazione).getTime();
      }
    });
  }

  selezionaProgetto(progetto: ProgettoResponse): void {
    this.progettoSelezionato = progetto;
    this.caricaIssue();
  }

  onNuovaIssue() {
    this.router.navigate(['/issue/nuova'], {
      queryParams: { idProgetto: this.progettoSelezionato?.id }
    });
  }

  onApriIssue(issue: IssueRiepilogoResponse) {
    this.router.navigate(['/issue', issue.id]);
  }

  onEsci() {
    this.authService.esci();
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
    this.applicaFiltri();
  }

  setFiltroPriorita(priorita: string) {
    this.filtroPriorita = this.filtroPriorita === priorita ? '' : priorita;
    this.applicaFiltri();
  }

  setFiltroStato(stato: string) {
    this.filtroStato = this.filtroStato === stato ? '' : stato;
    this.applicaFiltri();
  }

  getIconaTipo(tipo: string): string {
    switch (tipo) {
      case 'BUG': return '🐛';
      case 'QUESTION': return '❓';
      case 'DOCUMENTATION': return '📄';
      case 'FEATURE': return '✨';
      default: return '📋';
    }
  }
}
