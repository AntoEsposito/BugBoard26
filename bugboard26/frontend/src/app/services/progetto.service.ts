import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProgettoResponse } from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class ProgettoService {

  constructor(private readonly http: HttpClient) {}

  ottieniProgetti(): Observable<ProgettoResponse[]> {
    return this.http.get<ProgettoResponse[]>('/api/progetti');
  }
}
