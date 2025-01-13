import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from '../../base-service';
import { ApiConfiguration } from '../../api-configuration';
import { CategoryResponse } from '../../models/category-response';
import { PageResponse } from '../../models/page-response'; // You might need to create this model

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
  ): Observable<PageResponse<CategoryResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    sortBy.forEach((field, index) => {
      params = params.append('sortBy', field);
      params = params.append('direction', direction[index]);
    });

    return this.http.get<PageResponse<CategoryResponse>>(this.apiUrl, { params });
  }

  // Fetch a single category by ID
  getCategoryById(id: number): Observable<CategoryResponse> {
    return this.http.get<CategoryResponse>(`${this.apiUrl}/${id}`);
  }

  // Create a new category
  createCategory(category: CategoryResponse): Observable<CategoryResponse> {
    return this.http.post<CategoryResponse>(this.apiUrl, category);
  }

  // Update an existing category
  updateCategory(id: number, category: CategoryResponse): Observable<CategoryResponse> {
    return this.http.put<CategoryResponse>(`${this.apiUrl}/${id}`, category);
  }

  // Delete a category by ID
  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
