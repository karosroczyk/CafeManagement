import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../../../services/services/user/user.service';
import { UserResponse } from '../../../../services/models/user-response';

@Component({
  selector: 'app-profile-list',
  standalone: false,
  templateUrl: './profile-list.component.html',
  styleUrl: './profile-list.component.scss'
})
export class ProfileListComponent implements OnInit {
users: UserResponse[] = [];

  constructor(
    private router: Router,
    private userService: UserService) {}

  ngOnInit(): void {
    this.fetchUsers()
  }

  fetchUsers() {
    this.userService.getAllUsers().subscribe({
      next: (res) => {
        this.users = res.data ?? [];
    }});
  }

  editProfile(userId: number): void {
    this.router.navigate(['/cafe-admin/profile'], {
      queryParams: { id: userId }
    });
  }
}
