import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../../auth/auth.service';

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRole = route.data?.['role'];
  const userRoles = authService.getUserRoles()

  if (expectedRole && !authService.hasRole(expectedRole)) {
      if (userRoles.includes('ADMIN')) {
        router.navigate(['cafe-admin']);
      } else if (userRoles.includes('EMPLOYEE')) {
        router.navigate(['cafe-employee']);
      } else if (userRoles.includes('CLIENT')) {
        router.navigate(['cafe-client']);
      } else {
        router.navigate(['login']);
      }
    return false;
  }

  return true;
};
