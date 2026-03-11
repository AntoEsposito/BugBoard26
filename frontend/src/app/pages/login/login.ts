import { Component, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  email = '';
  password = '';
  mostraPassword = false;
  ricordami = false;
  caricamento = false;
  errore = '';
  mostraErrore = false;
  messaggioErrore = '';

  constructor(
    private readonly router: Router,
    private readonly authService: AuthService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  mostraNascondiPassword() {
    this.mostraPassword = !this.mostraPassword;
  }

  chiudiErrore(): void {
    this.mostraErrore = false;
  }

  onAccedi() {
    this.caricamento = true;
    this.errore = '';

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: (risposta) => {
        this.authService.salvaSessione(risposta);
        this.router.navigate(['/issue-list']);
      },
      error: (err) => {
        this.caricamento = false;
        if (err.status === 400) {
          this.messaggioErrore = 'Formato email non valido.';
        } else if (err.status === 401 || err.status === 404) {
          this.messaggioErrore = 'Email o password non corretti.';
        } else {
          this.messaggioErrore = 'Errore di connessione. Riprova più tardi.';
        }
        this.mostraErrore = true;
        this.cdr.detectChanges();
      }
    });
  }
}
