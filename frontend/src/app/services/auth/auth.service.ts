import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private router: Router) {}

  logout(): void {
    this.router.navigate(['login']);
  }

getUserRoles(): string[] {
  const token = localStorage.getItem('token');
  if (!token) return [];
  const decoded: any = jwtDecode(token);
  const authorities: string[] = decoded.authorities || [];

  // Remove ROLE_ prefix if present
  return authorities.map(role =>
    role.startsWith('ROLE_') ? role.substring(5) : role
  );
}

hasRole(expectedRole: string | string[]): boolean {
  const userRoles = this.getUserRoles();
  console.log('User Roles:', userRoles);
  console.log('Expected Role(s):', expectedRole);

  if (Array.isArray(expectedRole)) {
    return expectedRole.some(role => userRoles.includes(role));
  } else {
    return userRoles.includes(expectedRole);
  }
}
}
