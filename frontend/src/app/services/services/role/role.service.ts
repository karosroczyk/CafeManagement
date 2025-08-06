import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from '../../base-service';
import { ApiConfiguration } from '../../api-configuration';
import { RoleResponse } from '../../models/role';
import { PageResponse } from '../../models/page-response';

@Injectable({
  providedIn: 'root'
})
export class RoleService extends BaseService {
                        
  private readonly baseUrl = this.rootUrl + '/api/role';

  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }
  
    /**
     * Get all roles with pagination.
     */
    getAllRoles(
      page: number = 0,
      size: number = 4,
      sortBy: string[] = ['name'],
      direction: string[] = ['asc']
    ): Observable<PageResponse<RoleResponse>> {
      let params = new HttpParams()
        .set('page', page)
        .set('size', size)
        .set('sortBy', sortBy.join(','))
        .set('direction', direction.join(','));
    
      return this.http.get<PageResponse<RoleResponse>>(this.baseUrl, { params });
    }
    
    /**
     * Get a role by ID.
     */
    getRoleById(id: number): Observable<RoleResponse> {
      return this.http.get<RoleResponse>(`${this.baseUrl}/${id}`);
    }
    
    /**
     * Create a new role.
     */
    createRole(role: RoleResponse): Observable<RoleResponse> {
      return this.http.post<RoleResponse>(this.baseUrl, role);
    }
    
    /**
     * Update a role by ID.
     */
    updateRole(id: number, role: RoleResponse): Observable<RoleResponse> {
      return this.http.put<RoleResponse>(`${this.baseUrl}/${id}`, role);
    }
    
    /**
     * Delete a role by ID.
     */
    deleteRole(id: number): Observable<void> {
      return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }
}
