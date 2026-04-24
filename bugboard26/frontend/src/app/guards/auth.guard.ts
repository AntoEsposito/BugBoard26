import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// Guard funzionale che protegge le rotte riservate agli utenti autenticati
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAutenticato()) {
    return true;
  }

  // Utente non autenticato: reindirizza al login
  return router.createUrlTree(['/login']);
};
