import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

interface Issue {
  id: number;
  titolo: string;
  tipo: string;
  stato: string;
  priorita: string;
  autore: string;
  data: string;
  allegato: boolean;
}

@Component({
  selector: 'app-issue-list',
  imports: [FormsModule, CommonModule],
  templateUrl: './issue-list.html',
  styleUrl: './issue-list.css',
})
export class IssueList {
  testoRicerca = '';
  ordinamento = 'data_creazione';
  mostraFiltri = false;
  filtroTipo = '';
  filtroPriorita = '';
  filtroStato = '';

  issueList: Issue[] = [];

  constructor(private router: Router) {}

  // TODO: implementare la navigazione verso la creazione di una nuova issue
  onNuovaIssue() {
  }

  // TODO: implementare la navigazione verso il dettaglio dell'issue
  onApriIssue(issue: Issue) {
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
