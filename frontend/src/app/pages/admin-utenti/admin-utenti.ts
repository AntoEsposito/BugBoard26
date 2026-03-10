import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService, CreaUtenteRequest } from '../../services/auth.service';

@Component({
  selector: 'app-admin-utenti',
  imports: [FormsModule, CommonModule],
  templateUrl: './admin-utenti.html',
  styleUrl: './admin-utenti.css',
})
export class AdminUtenti {
  email = '';
  password = '';
  nome = '';
  cognome = '';
  ruolo = '';
  mostraPassword = false;

  caricamento = false;
  errore = '';
  successo = false;
  messaggioSuccesso = '';

  constructor(
    private readonly router: Router,
    private readonly authService: AuthService
  ) {}

  mostraNascondiPassword(): void {
    this.mostraPassword = !this.mostraPassword;
  }

  onCreaUtente(): void {
    this.errore = '';
    this.successo = false;

    if (this.nome.trim() === '') {
      this.errore = 'Il nome è obbligatorio.';
      return;
    }

    if (this.cognome.trim() === '') {
      this.errore = 'Il cognome è obbligatorio.';
      return;
    }

    if (this.email.trim() === '') {
      this.errore = 'L\'email è obbligatoria.';
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (this.email.length > 254 || !emailRegex.test(this.email)) {
      this.errore = 'Formato email non valido.';
      return;
    }

    if (this.password.trim() === '') {
      this.errore = 'La password è obbligatoria.';
      return;
    }

    if (this.password.length < 4) {
      this.errore = 'La password deve essere di almeno 4 caratteri.';
      return;
    }

    if (this.ruolo === '') {
      this.errore = 'Il ruolo è obbligatorio.';
      return;
    }

    const request: CreaUtenteRequest = {
      email: this.email.trim(),
      password: this.password,
      nome: this.nome.trim(),
      cognome: this.cognome.trim(),
      ruolo: this.ruolo
    };

    this.caricamento = true;
    this.authService.creaUtente(request).subscribe({
      next: (risposta) => {
        this.caricamento = false;
        this.successo = true;
        this.messaggioSuccesso = `Utente "${risposta.nome} ${risposta.cognome}" creato con successo!`;
        this.resetForm();
      },
      error: (err) => {
        this.caricamento = false;
        if (err.status === 409 || err.status === 400) {
          this.errore = 'Email già in uso o dati non validi.';
        } else if (err.status === 403) {
          this.errore = 'Non hai i permessi per creare utenti.';
        } else {
          this.errore = 'Errore durante la creazione. Riprova.';
        }
      }
    });
  }

  resetForm(): void {
    this.email = '';
    this.password = '';
    this.nome = '';
    this.cognome = '';
    this.ruolo = '';
    this.mostraPassword = false;
  }

  onTornaIndietro(): void {
    this.router.navigate(['/issue-list']);
  }
}
