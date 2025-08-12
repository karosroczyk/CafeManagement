import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { TokenService } from '../../token/token.service';

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const tokenService = inject(TokenService);
  const router = inject(Router);

  // Check authentication first
  if (!authService.isAuthenticated()) {
    router.navigate(['login']);
    return false;
  }

  const expectedRole = route.data?.['role'];
  const userRoles = tokenService.getRoles();

  // If route requires a role but user doesn't have it, redirect
  if (expectedRole && !authService.hasRole(expectedRole)) {
    redirectBasedOnRole(userRoles, router);
    return false;
  }

  return true;
};

function redirectBasedOnRole(userRoles: string[], router: Router) {
  if (userRoles.includes('ADMIN')) {
    router.navigate(['cafe-admin']);
  } else if (userRoles.includes('EMPLOYEE')) {
    router.navigate(['cafe-employee']);
  } else if (userRoles.includes('CLIENT')) {
    router.navigate(['cafe-client']);
  } else {
    router.navigate(['login']);
  }
}
