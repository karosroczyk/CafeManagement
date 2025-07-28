import { Role } from './role';

export interface UserResponse {
  id?: number;
  first_name?: string;
  last_name?: string;
  dateOfBirth?: string;
  email?: string;
  password?: string;
  accountLocked?: boolean;
  enabled?: boolean;   roles?: Role[];
  createdDate?: string;
  lastModifiedDate?: string;
}