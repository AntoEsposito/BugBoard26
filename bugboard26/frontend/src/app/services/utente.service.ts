import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UtenteResponse } from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class UtenteService {

  constructor(private readonly http: HttpClient) {}

  ottieniUtenti(): Observable<UtenteResponse[]> {
    return this.http.get<UtenteResponse[]>('/api/utenti');
  }
}
