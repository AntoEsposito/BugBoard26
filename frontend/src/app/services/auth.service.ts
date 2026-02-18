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

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly loginUrl = '/api/auth/login';

  constructor(private readonly http: HttpClient) {}

  login(credenziali: LoginRequest): Observable<LoginAnswer> {
    return this.http.post<LoginAnswer>(this.loginUrl, credenziali);
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
}
