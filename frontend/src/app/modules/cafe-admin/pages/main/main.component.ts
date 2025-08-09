import { Component } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-main',
  standalone: false,

  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent {

    constructor(
      private router: Router){}

handleButton1(){
  this.router.navigate(['cafe-admin/menucafe']);
}
handleButton2(){
  this.router.navigate(['cafe-admin/order']);
}
}
