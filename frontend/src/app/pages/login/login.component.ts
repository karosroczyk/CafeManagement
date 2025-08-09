import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationRequest} from '../../services/models/authentication-request';
import {AuthenticationService} from '../../services/services/authentication.service';
import {TokenService} from '../../services/token/token.service';
import {AuthService} from '../../services/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: false,

  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  authRequest: AuthenticationRequest = {email: '', password: ''};
  errorMsg: Array<string> = [];

    constructor(
      private router: Router,
      private authenticationService: AuthenticationService,
      private tokenService: TokenService,
      private authService: AuthService){}

  login() {
      this.errorMsg = [];
      this.authenticationService.authenticate({
        body: this.authRequest
      }).subscribe({
        next: (res) => {
          this.tokenService.token = res.token as string;
              const userRoles = this.authService.getUserRoles()

                  if (userRoles.includes('ADMIN')) {
                    this.router.navigate(['cafe-admin']);
                  } else if (userRoles.includes('EMPLOYEE')) {
                    this.router.navigate(['cafe-employee']);
                  } else if (userRoles.includes('CLIENT')) {
                    this.router.navigate(['cafe-client']);
                  } else {
                  console.log(userRoles)
                    this.router.navigate(['login']);
                  }

          //this.router.navigate(['cafe-client']);
        },
        error: (err) => {
          if (err.error.validationErrors) {
            this.errorMsg = err.error.validationErrors;
          } else {
            this.errorMsg.push(err.error.errorMsg);
          }
        }
      });
    }

    register() {
      this.router.navigate(['register']);
    }

}
