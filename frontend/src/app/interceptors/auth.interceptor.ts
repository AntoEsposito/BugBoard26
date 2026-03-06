import { HttpInterceptorFn } from '@angular/common/http';

// Interceptor funzionale che aggiunge il token JWT all'header Authorization
export const authInterceptor: HttpInterceptorFn = (richiesta, next) => {
  const token = localStorage.getItem('token');

  if (token) {
    // Clona la richiesta aggiungendo l'header di autenticazione
    const richiestaAutenticata = richiesta.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(richiestaAutenticata);
  }

  // Nessun token presente, inoltra la richiesta originale
  return next(richiesta);
};
