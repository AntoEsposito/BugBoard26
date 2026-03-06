import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  IssueRiepilogoResponse,
  IssueDettaglioResponse,
  CreaIssueRequest,
  ModificaIssueRequest
} from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class IssueService {

  constructor(private readonly http: HttpClient) {}

  ottieniIssuePerProgetto(idProgetto: number): Observable<IssueRiepilogoResponse[]> {
    const params = new HttpParams().set('idProgetto', idProgetto);
    return this.http.get<IssueRiepilogoResponse[]>('/api/issue', { params });
  }

  ottieniDettaglio(idIssue: number): Observable<IssueDettaglioResponse> {
    return this.http.get<IssueDettaglioResponse>(`/api/issue/${idIssue}`);
  }

  creaIssue(request: CreaIssueRequest): Observable<IssueRiepilogoResponse> {
    return this.http.post<IssueRiepilogoResponse>('/api/issue', request);
  }

  modificaIssue(idIssue: number, request: ModificaIssueRequest): Observable<IssueRiepilogoResponse> {
    return this.http.put<IssueRiepilogoResponse>(`/api/issue/${idIssue}`, request);
  }
}
