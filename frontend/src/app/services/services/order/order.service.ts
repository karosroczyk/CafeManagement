import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from '../../base-service';
import { ApiConfiguration } from '../../api-configuration';
import { OrderResponse } from '../../models/order-response';
import { MenuResponse } from '../../models/menu-response';
import { PageResponse } from '../../models/page-response'; // You might need to create this model
import { HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class OrderService extends BaseService {
  private apiUrl = this.rootUrl + '/orders';

  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  // Get all orders with pagination
  getAllOrders(page: number = 0, size: number = 2, sortBy: string[] = ['customerId'], direction: string[] = ['asc']): Observable<PageResponse<OrderResponse>> {
    return this.http.get<PageResponse<OrderResponse>>(`${this.apiUrl}`);
  }

  // Get a single order by ID
  getOrderById(id: number): Observable<OrderResponse> {
    return this.http.get<OrderResponse>(`${this.apiUrl}/${id}`);
  }

  // Get orders by customer ID
  getOrdersByCustomerId(customerId: number): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.apiUrl}/customer/${customerId}`);
  }

  // Get all menu items with pagination
  getAllMenuItems(page: number = 0, size: number = 2, sortBy: string[] = ['customerId'], direction: string[] = ['asc']): Observable<PageResponse<MenuResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy.join(','))
      .set('direction', direction.join(','));

    return this.http.get<PageResponse<MenuResponse>>(`${this.apiUrl}/menuitems`, { params });
  }

  // Create a new order
  createOrder(order: OrderResponse): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(`${this.apiUrl}`, order);
  }

  // Place an order with menu items and quantities
  placeOrder(customerId: number, menuItemIds: number[], quantitiesOfMenuItems: number[]): Observable<OrderResponse> {
    const body = {
      customerId: customerId,
      menuItemIds: menuItemIds,
      quantitiesOfMenuItems: quantitiesOfMenuItems
      };

    const token = localStorage.getItem('token');

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<OrderResponse>(`${this.apiUrl}/items`,
        body, { headers });
  }

  // Update an order
  updateOrder(id: number, order: OrderResponse): Observable<OrderResponse> {
    return this.http.put<OrderResponse>(`${this.apiUrl}/${id}`, order);
  }

  // Delete an order
  deleteOrder(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
