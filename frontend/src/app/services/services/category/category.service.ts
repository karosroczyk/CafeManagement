import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from '../../base-service';
import { ApiConfiguration } from '../../api-configuration';

export interface PaginatedResponse<T> {
  data: T[];
  totalPages: number;
  totalElements: number;
  currentPage: number;
  pageSize: number;
}

export interface Category {
  id?: number;
  name: string;
  description: string;
}

@Injectable({
  providedIn: 'root',
})
export class CategoryService extends BaseService {

  private readonly apiUrl = this.rootUrl + '/api/categories';

  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  // Fetch paginated categories
  getAllCategories(
    page: number = 0,
    size: number = 20,
    sortBy: string[] = ['name'],
    direction: string[] = ['asc']
  ): Observable<PaginatedResponse<Category>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    sortBy.forEach((field, index) => {
      params = params.append('sortBy', field);
      params = params.append('direction', direction[index]);
    });

    return this.http.get<PaginatedResponse<Category>>(this.apiUrl, { params });
  }

  // Fetch a single category by ID
  getCategoryById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.apiUrl}/${id}`);
  }

  // Create a new category
  createCategory(category: Category): Observable<Category> {
    return this.http.post<Category>(this.apiUrl, category);
  }

  // Update an existing category
  updateCategory(id: number, category: Category): Observable<Category> {
    return this.http.put<Category>(`${this.apiUrl}/${id}`, category);
  }

  // Delete a category by ID
  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
