import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreaCommentoRequest, CommentoResponse } from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class CommentoService {

  constructor(private readonly http: HttpClient) {}

  aggiungiCommento(idIssue: number, request: CreaCommentoRequest): Observable<CommentoResponse> {
    return this.http.post<CommentoResponse>(`/api/issue/${idIssue}/commenti`, request);
  }
}
