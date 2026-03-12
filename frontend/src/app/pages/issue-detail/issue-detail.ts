import { Component, OnInit, ChangeDetectorRef, HostListener } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import {
  IssueDettaglioResponse,
  CreaCommentoRequest,
  CommentoResponse,
  ModificaIssueRequest,
  UtenteResponse
} from '../../models/api.models';
import { IssueService } from '../../services/issue.service';
import { CommentoService } from '../../services/commento.service';
import { UtenteService } from '../../services/utente.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-issue-detail',
  imports: [FormsModule, CommonModule, DatePipe],
  templateUrl: './issue-detail.html',
  styleUrl: './issue-detail.css',
})
export class IssueDetail implements OnInit {
  issue: IssueDettaglioResponse | null = null;
  caricamento = true;
  erroreCaricamento = '';

  // Commenti
  nuovoCommento = '';
  invioCommento = false;
  erroreCommento = '';
  successoCommento = false;

  // Fullscreen immagine
  immagineFullscreen = false;

  // Modifica
  modalitaModifica = false;
  puoModificare = false;
  salvataggioInCorso = false;
  erroreModifica = '';
  successoModifica = false;

  // Campi modifica
  statoModifica = '';
  tipoModifica = '';
  prioritaModifica = '';
  descrizioneModifica = '';
  assegnatariModifica: number[] = [];
  utentiDisponibili: UtenteResponse[] = [];

  // Dropdown assegnatari
  dropdownAssegnatariAperto = false;
  filtroAssegnatari = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly issueService: IssueService,
    private readonly commentoService: CommentoService,
    private readonly utenteService: UtenteService,
    protected readonly authService: AuthService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (Number.isNaN(id)) {
      this.router.navigate(['/issue-list']);
      return;
    }
    this.caricaDettaglio(id);
  }

  caricaDettaglio(id: number): void {
    this.caricamento = true;
    this.erroreCaricamento = '';
    this.issueService.ottieniDettaglio(id).subscribe({
      next: (issue) => {
        this.issue = issue;
        this.caricamento = false;
        this.calcolaPermessi();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.caricamento = false;
        this.cdr.detectChanges();
        if (err.status === 403) {
          this.erroreCaricamento = 'Non hai i permessi per visualizzare questa issue.';
        } else if (err.status === 404) {
          this.erroreCaricamento = 'Issue non trovata.';
        } else {
          this.erroreCaricamento = 'Errore durante il caricamento della issue.';
        }
      }
    });
  }

  calcolaPermessi(): void {
    if (!this.issue) return;

    if (this.authService.isAdmin()) {
      this.puoModificare = true;
      return;
    }

    const emailCorrente = this.authService.getEmail();
    this.puoModificare = this.issue.assegnatari.some(a => a.email === emailCorrente);
  }

  attivaModifica(): void {
    if (!this.issue) return;

    this.modalitaModifica = true;
    this.erroreModifica = '';
    this.successoModifica = false;

    // Precompila i campi con i valori attuali
    this.statoModifica = this.issue.stato;
    this.tipoModifica = this.issue.tipo;
    this.prioritaModifica = this.issue.priorita;
    this.descrizioneModifica = this.issue.descrizione || '';
    this.assegnatariModifica = this.issue.assegnatari.map(a => a.id);

    // Carica tutti gli utenti per la selezione assegnatari (solo admin)
    if (this.authService.isAdmin()) {
      this.utenteService.ottieniUtenti().subscribe({
        next: (utenti) => {
          this.utentiDisponibili = utenti;
          this.cdr.detectChanges();
        },
        error: () => {
          this.utentiDisponibili = this.issue!.assegnatari;
          this.cdr.detectChanges();
        }
      });
    }
  }

  annullaModifica(): void {
    this.modalitaModifica = false;
    this.erroreModifica = '';
    this.dropdownAssegnatariAperto = false;
    this.filtroAssegnatari = '';
  }

  toggleAssegnatario(idUtente: number): void {
    const index = this.assegnatariModifica.indexOf(idUtente);
    if (index === -1) {
      this.assegnatariModifica.push(idUtente);
    } else {
      this.assegnatariModifica.splice(index, 1);
    }
  }

  toggleDropdownAssegnatari(): void {
    this.dropdownAssegnatariAperto = !this.dropdownAssegnatariAperto;
    if (!this.dropdownAssegnatariAperto) this.filtroAssegnatari = '';
  }

  @HostListener('document:click')
  onDocumentClick(): void {
    if (this.dropdownAssegnatariAperto) {
      this.dropdownAssegnatariAperto = false;
      this.filtroAssegnatari = '';
    }
  }

  get utentiFiltrati(): UtenteResponse[] {
    if (!this.filtroAssegnatari.trim()) return this.utentiDisponibili;
    const filtro = this.filtroAssegnatari.toLowerCase();
    return this.utentiDisponibili.filter(u =>
      (u.nome + ' ' + u.cognome).toLowerCase().includes(filtro)
    );
  }

  get etichettaAssegnatari(): string {
    const n = this.assegnatariModifica.length;
    if (n === 0) return 'Nessun assegnatario';
    if (n === 1) {
      const u = this.utentiDisponibili.find(ut => ut.id === this.assegnatariModifica[0]);
      return u ? u.nome + ' ' + u.cognome : '1 assegnatario selezionato';
    }
    return n + ' assegnatari selezionati';
  }

  onSalvaModifica(): void {
    if (!this.issue) return;

    this.erroreModifica = '';
    this.salvataggioInCorso = true;

    const request: ModificaIssueRequest = {
      stato: this.statoModifica,
      tipo: this.tipoModifica,
      priorita: this.prioritaModifica,
      descrizione: this.descrizioneModifica,
      idAssegnatari: this.assegnatariModifica
    };

    this.issueService.modificaIssue(this.issue.id, request).subscribe({
      next: () => {
        this.salvataggioInCorso = false;
        this.modalitaModifica = false;
        this.successoModifica = true;
        this.cdr.detectChanges();
        setTimeout(() => { this.successoModifica = false; this.cdr.detectChanges(); }, 3000);
        // Ricarica il dettaglio aggiornato
        this.caricaDettaglio(this.issue!.id);
      },
      error: (err) => {
        this.salvataggioInCorso = false;
        this.cdr.detectChanges();
        if (err.status === 403) {
          this.erroreModifica = 'Non hai i permessi per modificare questa issue.';
        } else {
          this.erroreModifica = 'Errore durante il salvataggio. Riprova.';
        }
      }
    });
  }

  // --- Commenti ---

  onInviaCommento(): void {
    if (this.nuovoCommento.trim() === '' || !this.issue) return;

    this.erroreCommento = '';
    this.invioCommento = true;

    const request: CreaCommentoRequest = {
      contenuto: this.nuovoCommento.trim()
    };

    this.commentoService.aggiungiCommento(this.issue.id, request).subscribe({
      next: (commento) => {
        this.issue!.commenti.unshift(commento);
        this.nuovoCommento = '';
        this.invioCommento = false;
        this.successoCommento = true;
        this.cdr.detectChanges();
        setTimeout(() => { this.successoCommento = false; this.cdr.detectChanges(); }, 2000);
      },
      error: () => {
        this.invioCommento = false;
        this.cdr.detectChanges();
        this.erroreCommento = 'Errore durante l\'invio del commento. Riprova.';
      }
    });
  }

  tornaAllaLista(): void {
    this.router.navigate(['/issue-list']);
  }

  // --- Utility ---

  getIconaTipo(tipo: string): string {
    switch (tipo) {
      case 'BUG': return '🐛';
      case 'QUESTION': return '❓';
      case 'DOCUMENTATION': return '📄';
      case 'FEATURE': return '✨';
      default: return '📋';
    }
  }

  getClasseStato(stato: string): string {
    switch (stato) {
      case 'TODO': return 'stato-todo';
      case 'IN_PROGRESS': return 'stato-in-progress';
      case 'DONE': return 'stato-done';
      default: return '';
    }
  }

  getEtichettaStato(stato: string): string {
    switch (stato) {
      case 'TODO': return 'To Do';
      case 'IN_PROGRESS': return 'In Progress';
      case 'DONE': return 'Done';
      default: return stato;
    }
  }

  getClassePriorita(priorita: string): string {
    switch (priorita) {
      case 'HIGH': return 'priorita-alta';
      case 'MEDIUM': return 'priorita-media';
      case 'LOW': return 'priorita-bassa';
      default: return '';
    }
  }

  getInizialiUtente(commento: CommentoResponse): string {
    if (this.issue) {
      const utente = this.issue.assegnatari.find(a => a.id === commento.idUtenteCreatore);
      if (utente) {
        return (utente.nome[0] + utente.cognome[0]).toUpperCase();
      }
    }
    return '??';
  }

  getNomeUtente(commento: CommentoResponse): string {
    if (this.issue) {
      const utente = this.issue.assegnatari.find(a => a.id === commento.idUtenteCreatore);
      if (utente) {
        return utente.nome + ' ' + utente.cognome;
      }
    }
    return 'Utente sconosciuto';
  }
}
