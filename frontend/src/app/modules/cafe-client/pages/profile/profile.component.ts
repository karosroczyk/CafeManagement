import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../../services/services/user/user.service';
import { RoleService } from '../../../../services/services/role/role.service';
import { AuthService } from '../../../../services/auth/auth.service';
import { TokenService } from '../../../../services/token/token.service';
import { PageResponse } from '../../../../services/models/page-response';
import { UserResponse } from '../../../../services/models/user-response';
import { RoleResponse } from '../../../../services/models/role';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  user: UserResponse = {};
  pageResponseUserResponse: PageResponse<UserResponse> = {};

  availableRoles : RoleResponse[] = [];

  selectedRoleId: number = 1; // default to USER
  successMessage: string = '';
  failureMessage: string = '';

  constructor(
    private userService: UserService,
    private roleService: RoleService,
    private authService: AuthService,
    private tokenService: TokenService) {}

  ngOnInit(): void {
    const userEmail = this.tokenService.getEmail();
    if (!userEmail) {
      console.error('No email found in token');
      return;
    }

    this.userService.getUserByEmail(userEmail).subscribe({
      next: user => {
        this.user = user;
        this.selectedRoleId = this.user.roles?.[0]?.id ?? 1;
      },
      error: err => {
        console.error('Failed to load user', err);
      }
    });

    this.roleService.getAllRoles().subscribe({
      next: response => {
        this.availableRoles = response.data ?? [];
        console.log(response.data)
      },
      error: err => {
        console.error('Failed to load availableRoles', err);
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

    this.userService.updateUser(userId, updatedUser).subscribe({
      next: () => {
        this.successMessage = 'Profile updated successfully.';
        setTimeout(() => this.successMessage = '', 5000);
      },
      error: err => {
        this.failureMessage = 'Failed to update account.';
        setTimeout(() => this.failureMessage = '', 5000);
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
            this.successMessage = 'Account deleted successfully.';
            setTimeout(() => this.successMessage = '', 5000);
            this.authService.logout();
        },
        error: err => {
            this.failureMessage = 'Failed to delete account.';
            setTimeout(() => this.failureMessage = '', 5000);
        }
      });
    }
}
