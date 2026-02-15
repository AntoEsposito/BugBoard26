import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

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

  mostraNascondiPassword() {
    this.mostraPassword = !this.mostraPassword;
  }

  onAccedi() {
    this.caricamento = true;
    this.errore = '';

    // TODO: implementare la chiamata al backend
    console.log('Login con:', this.email, this.password);

    setTimeout(() => {
      this.caricamento = false;
    }, 1000);
  }
}
