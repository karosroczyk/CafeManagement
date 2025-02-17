import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu-cafe',
  standalone: false,

  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {
  user = {
    firstName: '',
    lastName: '',
    dateOfBirth: '',
    email: '',
    password: ''
  };

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
  }

  updateProfile(): void {
  }

  deleteAccount(): void {
  }
}
