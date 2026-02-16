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
  avatarNonCaricato = false;
  testoRicerca = '';

  ordinamento = 'data_creazione';
  mostraFiltri = false;
  filtroTipo = '';
  filtroPriorita = '';
  filtroStato = '';

  issueList: Issue[] = [];

  constructor(private readonly router: Router) {}

  onNuovaIssue() {
    this.router.navigate(['/issue/nuova']);
  }

  onApriIssue(issue: Issue) {
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
