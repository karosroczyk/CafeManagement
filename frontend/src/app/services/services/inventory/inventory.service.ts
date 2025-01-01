import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from '../../base-service';
import { ApiConfiguration } from '../../api-configuration';

interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

interface InventoryItem {
  id: number;
  menuItemId: number;
  stockLevel: number;
  isAvailable: boolean;
}

@Injectable({ providedIn: 'root' })
export class InventoryService extends BaseService {

  private baseUrl = '/api/inventory';

  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /**
   * Get all inventory items with pagination.
   */
  getAllInventoryItems(
    page: number = 0,
    size: number = 20,
    sortBy: string[] = ['menuItemId'],
    direction: string[] = ['asc']
  ): Observable<PaginatedResponse<InventoryItem>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy.join(','))
      .set('direction', direction.join(','));

    return this.http.get<PaginatedResponse<InventoryItem>>(this.rootUrl+'/api/inventory', { params });
  }

  /**
   * Get an inventory item by ID.
   */
  getInventoryItemById(id: number): Observable<InventoryItem> {
    return this.http.get<InventoryItem>(`${this.baseUrl}/${id}`);
  }

  /**
   * Check the availability of inventory items by menu item IDs and quantities.
   */
  getInventoryItemAvailability(menuItemIds: number[], quantities: number[]): Observable<boolean[]> {
    let params = new HttpParams()
      .set('menuItemIds', menuItemIds.join(','))
      .set('quantitiesOfMenuItems', quantities.join(','));

    return this.http.get<boolean[]>(`${this.baseUrl}/availability`, { params });
  }

  /**
   * Create a new inventory item.
   */
  createInventoryItem(inventoryItem: InventoryItem): Observable<InventoryItem> {
    return this.http.post<InventoryItem>(this.baseUrl, inventoryItem);
  }

  /**
   * Update an inventory item by ID.
   */
  updateInventoryItem(id: number, inventoryItem: InventoryItem): Observable<InventoryItem> {
    return this.http.put<InventoryItem>(`${this.baseUrl}/${id}`, inventoryItem);
  }

  /**
   * Reduce stock by menu item IDs and quantities.
   */
  reduceStock(menuItemIds: number[], quantities: number[]): Observable<InventoryItem[]> {
    let params = new HttpParams()
      .set('menuItemIds', menuItemIds.join(','))
      .set('quantitiesOfMenuItems', quantities.join(','));

    return this.http.put<InventoryItem[]>(`${this.baseUrl}/reduce`, null, { params });
  }

  /**
   * Add stock for an inventory item.
   */
  addStock(id: number, quantity: number): Observable<InventoryItem> {
    return this.http.put<InventoryItem>(`${this.baseUrl}/${id}/add`, quantity);
  }

  /**
   * Delete an inventory item by ID.
   */
  deleteInventoryItem(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
