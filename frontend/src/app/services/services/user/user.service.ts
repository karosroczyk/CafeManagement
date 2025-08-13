import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from '../../base-service';
import { ApiConfiguration } from '../../api-configuration';
import { UserResponse } from '../../models/user-response';
import { PageResponse } from '../../models/page-response';
import { TokenService } from '../../token/token.service';

@Injectable({
  providedIn: 'root'
})
export class UserService extends BaseService {

  private readonly baseUrl = this.rootUrl + '/api/user';

    constructor(
        config: ApiConfiguration,
        http: HttpClient,
        private tokenService: TokenService
        ) {
        super(config, http);
    }
  
    /**
     * Get all users with pagination.
     */
    getAllUsers(
      page: number = 0,
      size: number = 20,
      sortBy: string[] = ['id'],
      direction: string[] = ['asc']
    ): Observable<PageResponse<UserResponse>> {
      let params = new HttpParams()
        .set('page', page)
        .set('size', size)
        .set('sortBy', sortBy.join(','))
        .set('direction', direction.join(','));
    
      return this.http.get<PageResponse<UserResponse>>(this.baseUrl, { params });
    }
    
    /**
     * Get a user by ID.
     */
    getUserById(id: number): Observable<UserResponse> {
      return this.http.get<UserResponse>(`${this.baseUrl}/${id}`);
    }

    /**
     * Get a user by email.
     */
    getUserByEmail(email: string): Observable<UserResponse> {
      return this.http.post<UserResponse>(`${this.baseUrl}/email`, { email });
    }

    /**
    * Get the current logged-in user based on the token email.
    * Returns an Observable of UserResponse or an error if no email found.
    */
    getCurrentUser(): Observable<UserResponse> {
        const userEmail = this.tokenService.getEmail();
        if (!userEmail) {
            console.error('No email found in token');
        }
        return this.getUserByEmail(userEmail);
    }
    
    /**
     * Create a new user.
     */
    createUser(user: UserResponse): Observable<UserResponse> {
      return this.http.post<UserResponse>(this.baseUrl, user);
    }
    
    /**
     * Update a user by ID.
     */
    updateUser(id: number, user: UserResponse): Observable<UserResponse> {
      return this.http.put<UserResponse>(`${this.baseUrl}/${id}`, user);
    }
    
    /**
     * Delete a user by ID.
     */
    deleteUser(id: number): Observable<void> {
      return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }
}
