import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { TokenService } from '../token/token.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private router: Router,
    private tokenService: TokenService
  ) {}

  logout(): void {
    this.tokenService.clearToken();
    this.router.navigate(['login']);
  }

  hasRole(expectedRole: string | string[]): boolean {
    const userRoles = this.tokenService.getRoles();

    if (Array.isArray(expectedRole)) {
      return expectedRole.some(role => userRoles.includes(role));
    }
    return userRoles.includes(expectedRole);
  }

  isAuthenticated(): boolean {
    return this.tokenService.isValid();
  }
}