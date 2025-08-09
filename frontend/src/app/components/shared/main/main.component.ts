import { Component, Input } from '@angular/core';
import {Router} from "@angular/router";
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-main',
  standalone: false,

  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent {

    @Input() firstButton!: string;
    @Input() secondButton!: string;

  constructor(
    private router: Router,
    public authService: AuthService
  ) {}

handleButton1(){
  this.router.navigate([`${this.firstButton}`]);
}
handleButton2(){
  this.router.navigate([`${this.secondButton}`]);
}

  get button2Label(): string {
    if (this.authService.hasRole('EMPLOYEE')) {
      return 'Orders';
    } else if (this.authService.hasRole('CLIENT')) {
      return 'Create an order';
    }
    return 'Create an order';
  }

}
