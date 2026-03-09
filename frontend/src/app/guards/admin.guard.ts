import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// Guard funzionale che protegge le rotte riservate agli amministratori
export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAutenticato() && authService.isAdmin()) {
    return true;
  }

  // Utente non admin: reindirizza alla lista issue
  return router.createUrlTree(['/issue-list']);
};
