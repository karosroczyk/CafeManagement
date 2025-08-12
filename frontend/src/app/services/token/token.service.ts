import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private readonly TOKEN_KEY = 'token';
  private jwtHelper = new JwtHelperService();

  setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  clearToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  isValid(): boolean {
    const token = this.getToken();
    if (!token) return false;

    if (this.jwtHelper.isTokenExpired(token)) {
      this.clearToken();
      return false;
    }
    return true;
  }

  decodeToken(): any | null {
    const token = this.getToken();
    return token ? jwtDecode(token) : null;
  }

  getEmail(): string{
    const decoded = this.decodeToken();
    return decoded?.sub;
  }

  getRoles(): string[] {
    const decoded = this.decodeToken();
    if (!decoded?.authorities) return [];
    return decoded.authorities.map((role: string) =>
      role.startsWith('ROLE_') ? role.substring(5) : role
    );
  }
}
