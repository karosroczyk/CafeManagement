import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from '../../base-service';
import { ApiConfiguration } from '../../api-configuration';
import { MenuResponse } from '../../models/menu-response';
import { PageResponse } from '../../models/page-response';

@Injectable({
  providedIn: 'root'
})
export class MenuService extends BaseService {

  private baseUrl = this.rootUrl + '/api/menuitems';

  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /**
   * Get all menu items with pagination.
   */
  getAllMenuItems(
    page: number = 0,
    size: number = 2,
    sortBy: string[] = ['name'],
    direction: string[] = ['asc']
  ): Observable<PageResponse<MenuResponse>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy.join(','))
      .set('direction', direction.join(','));

    return this.http.get<PageResponse<MenuResponse>>(this.baseUrl, { params });
  }

  /**
   * Get a menu item by ID.
   */
  getMenuItemById(id: number): Observable<MenuResponse> {
    return this.http.get<MenuResponse>(`${this.baseUrl}/${id}`);
  }

  /**
   * Get the price of a menu item by ID.
   */
  getMenuItemPriceById(id: number): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/${id}/price`);
  }

  /**
   * Get menu items by category name.
   */
  getMenuItemsByCategoryName(
    categoryName: string,
    page: number = 0,
    size: number = 2,
    sortBy: string[] = ['name'],
    direction: string[] = ['asc']
  ): Observable<PageResponse<MenuResponse>> {
    let params = new HttpParams()
      .set('categoryName', categoryName)
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy.join(','))
      .set('direction', direction.join(','));

    return this.http.get<PageResponse<MenuResponse>>(`${this.baseUrl}/filter/category-name`, { params });
  }

  /**
   * Create a new menu item.
   */
  createMenuItem(menuItem: MenuResponse): Observable<MenuResponse> {
    return this.http.post<MenuResponse>(this.baseUrl, menuItem);
  }

  /**
   * Update a menu item by ID.
   */
  updateMenuItem(id: number, menuItem: MenuResponse): Observable<MenuResponse> {
    return this.http.put<MenuResponse>(`${this.baseUrl}/${id}`, menuItem);
  }

  /**
   * Delete a menu item by ID.
   */
  deleteMenuItem(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
