import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../../services/services/user/user.service';
import { AuthService } from '../../../../services/auth/auth.service';
import { TokenService } from '../../../../services/token/token.service';
import { PageResponse } from '../../../../services/models/page-response';
import { UserResponse } from '../../../../services/models/user-response';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  user: UserResponse = {};
  pageResponseUserResponse: PageResponse<UserResponse> = {};

  availableRoles = [
      { id: 1, name: 'USER' },
      { id: 2, name: 'ADMIN' },
      { id: 3, name: 'MANAGER' }
    ];

  selectedRoleId: number = 1; // default to USER

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private tokenService: TokenService) {}

  ngOnInit(): void {
    const userEmail = this.tokenService.getEmailFromToken();
      this.userService.getUserByEmail(userEmail).subscribe({
        next: user => {
          this.user = user;
          console.log('Current user:', user);
        },
        error: err => {
          console.error('Failed to load user', err);
        }
      });
    this.selectedRoleId = this.user.roles?.[0]?.id ?? 1;
  }

  updateProfile(): void {
    const userId = this.user.id;
    if (!userId) {
      console.error('User ID not found. Cannot update account.');
      return;
    }

    const updatedUser = {
      id: this.user.id,
      first_name: this.user.first_name,
      last_name: this.user.last_name,
      email: this.user.email,
      password: this.user.password,
      dateOfBirth: this.user.dateOfBirth,
      roles: [this.availableRoles.find(role => role.id === this.selectedRoleId)!],
    };

    console.log('Current after update:', updatedUser);

    this.userService.updateUser(userId, updatedUser).subscribe({
      next: () => {
        console.log('Account updated successfully.');
      },
      error: err => {
        console.error('Failed to update account', err);
      }
    });
  }

    deleteAccount(): void {
      const userId = this.user.id;
      if (!userId) {
        console.error('User ID not found. Cannot delete account.');
        return;
      }

      this.userService.deleteUser(userId).subscribe({
        next: () => {
          console.log('Account deleted successfully.');
          this.authService.logout();
        },
        error: err => {
          console.error('Failed to delete account', err);
        }
      });
    }
}
