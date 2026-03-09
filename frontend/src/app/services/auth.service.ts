import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginAnswer {
  token: string;
  tokenType: string;
  email: string;
  nome: string;
  cognome: string;
  userRole: string;
  expireTime: number;
}

export interface CreaUtenteRequest {
  email: string;
  password: string;
  nome: string;
  cognome: string;
  ruolo: string;
}

export interface UtenteAuthResponse {
  id: number;
  email: string;
  nome: string;
  cognome: string;
  ruolo: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly loginUrl = '/api/auth/login';
  private readonly utentiUrl = '/api/auth/utenti';

  constructor(private readonly http: HttpClient) {}

  login(credenziali: LoginRequest): Observable<LoginAnswer> {
    return this.http.post<LoginAnswer>(this.loginUrl, credenziali);
  }

  creaUtente(request: CreaUtenteRequest): Observable<UtenteAuthResponse> {
    return this.http.post<UtenteAuthResponse>(this.utentiUrl, request);
  }

  salvaSessione(risposta: LoginAnswer): void {
    localStorage.setItem('token', risposta.token);
    localStorage.setItem('email', risposta.email);
    localStorage.setItem('nome', risposta.nome);
    localStorage.setItem('cognome', risposta.cognome);
    localStorage.setItem('ruolo', risposta.userRole);
  }

  esci(): void {
    localStorage.clear();
  }

  isAutenticato(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRuolo(): string | null {
    return localStorage.getItem('ruolo');
  }

  getNome(): string | null {
    return localStorage.getItem('nome');
  }

  getCognome(): string | null {
    return localStorage.getItem('cognome');
  }

  getEmail(): string | null {
    return localStorage.getItem('email');
  }

  isAdmin(): boolean {
    return this.getRuolo() === 'ROLE_ADMIN';
  }

  getIniziali(): string {
    const nome = this.getNome();
    const cognome = this.getCognome();
    if (nome && cognome) {
      return (nome[0] + cognome[0]).toUpperCase();
    }
    return '??';
  }
}
