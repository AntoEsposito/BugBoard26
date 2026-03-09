export interface ProgettoResponse {
  id: number;
  nome: string;
  descrizione: string;
}

export interface UtenteResponse {
  id: number;
  email: string;
  nome: string;
  cognome: string;
}

export interface CommentoResponse {
  id: number;
  idIssue: number;
  idUtenteCreatore: number;
  contenuto: string;
  dataCreazione: string;
}

export interface IssueRiepilogoResponse {
  id: number;
  idProgetto: number;
  idUtenteCreatore: number;
  titolo: string;
  stato: string;
  tipo: string;
  priorita: string;
  descrizione: string;
  dataCreazione: string;
  dataUltimaModifica: string;
  immaginePath?: string;
}

export interface IssueDettaglioResponse extends IssueRiepilogoResponse {
  assegnatari: UtenteResponse[];
  commenti: CommentoResponse[];
}

export interface CreaIssueRequest {
  idProgetto: number;
  titolo: string;
  tipo: string;
  descrizione?: string;
  priorita?: string;
}

export interface ModificaIssueRequest {
  descrizione?: string;
  stato?: string;
  priorita?: string;
  tipo?: string;
  idAssegnatari?: number[];
  rimuoviImmagine?: boolean;
}

export interface CreaCommentoRequest {
  contenuto: string;
}
