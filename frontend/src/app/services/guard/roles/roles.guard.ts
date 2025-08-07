import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../../auth/auth.service';

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRole = route.data?.['role'];
  console.log(authService.hasRole(expectedRole))

  if (expectedRole && !authService.hasRole(expectedRole)) {
    router.navigate(['cafe-client']);
    return false;
  }

  return true;
};
