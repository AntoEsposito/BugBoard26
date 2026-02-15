import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

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

  constructor(private router: Router) {}

  mostraNascondiPassword() {
    this.mostraPassword = !this.mostraPassword;
  }

  onAccedi() {
    this.caricamento = true;
    this.errore = '';

    if (this.email === 'admin@bugboard26.com' && this.password === 'demo123') {
      this.router.navigate(['/issue-list']);
    } else {
      this.errore = 'Email o password non corretti.';
      this.caricamento = false;
    }
  }
}
