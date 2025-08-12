import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationRequest } from '../../services/models/authentication-request';
import { AuthenticationService } from '../../services/services/authentication.service';
import { TokenService } from '../../services/token/token.service';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  authRequest: AuthenticationRequest = { email: '', password: '' };
  errorMsg: string[] = [];

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private tokenService: TokenService,
    private authService: AuthService
  ) {}

  login(): void {
    this.errorMsg = [];

    this.authenticationService.authenticate({ body: this.authRequest }).subscribe({
      next: (res) => {
        // Save token
        this.tokenService.setToken(res.token as string);

        // Get roles from token
        const userRoles = this.tokenService.getRoles();

        // Navigate based on role
        if (userRoles.includes('ADMIN')) {
          this.router.navigate(['cafe-admin']);
        } else if (userRoles.includes('EMPLOYEE')) {
          this.router.navigate(['cafe-employee']);
        } else if (userRoles.includes('CLIENT')) {
          this.router.navigate(['cafe-client']);
        } else {
          console.warn('Unknown role(s) for user:', userRoles);
          this.router.navigate(['login']);
        }
      },
      error: (err) => {
        if (err.error?.validationErrors) {
          this.errorMsg = err.error.validationErrors;
        } else if (err.error?.errorMsg) {
          this.errorMsg.push(err.error.errorMsg);
        } else {
          this.errorMsg.push('Login failed. Please try again.');
        }
      }
    });
  }

  register(): void {
    this.router.navigate(['register']);
  }
}
