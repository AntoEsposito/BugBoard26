import { Component } from '@angular/core';
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

  constructor(
    private readonly router: Router,
    private readonly authService: AuthService
  ) {}

  mostraNascondiPassword() {
    this.mostraPassword = !this.mostraPassword;
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
        if (err.status === 401 || err.status === 404) {
          this.errore = 'Email o password non corretti.';
        } else {
          this.errore = 'Errore di connessione. Riprova più tardi.';
        }
        this.caricamento = false;
      }
    });
  }
}
