import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
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

  immagineSelezionata: File | null = null;
  anteprimaImmagine: string | null = null;

  caricamento: boolean = false;
  errore: string = '';
  successo: boolean = false;
  mostraConferma: boolean = false;

  @ViewChild('inputImmagine') inputImmagine!: ElementRef<HTMLInputElement>;

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

    this.mostraConferma = true;
  }

  confermaCreazione(): void {
    this.mostraConferma = false;

    const request: CreaIssueRequest = {
      idProgetto: this.idProgetto,
      titolo: this.titolo.trim(),
      tipo: this.tipo,
      descrizione: this.descrizione.trim() === '' ? undefined : this.descrizione.trim(),
      priorita: this.priorita === '' ? undefined : this.priorita,
    };

    this.caricamento = true;
    this.issueService.creaIssue(request, this.immagineSelezionata ?? undefined).subscribe({
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

  annullaConferma(): void {
    this.mostraConferma = false;
  }

  onFileSelezionato(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.immagineSelezionata = input.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.anteprimaImmagine = reader.result as string;
      };
      reader.readAsDataURL(this.immagineSelezionata);
    }
  }

  rimuoviImmagine(): void {
    this.immagineSelezionata = null;
    this.anteprimaImmagine = null;
  }

  apriSelezioneFile(): void {
    this.inputImmagine.nativeElement.click();
  }

  onAnnulla(): void {
    this.router.navigate(['/issue-list']);
  }
}
