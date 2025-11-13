import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from '../../base-service';
import { ApiConfiguration } from '../../api-configuration';
import { OrderItemResponse } from '../../models/order-item-response';
import { HttpHeaders } from '@angular/common/http';


@Injectable({
  providedIn: 'root'
})
export class OrderMenuItemService extends BaseService {
   private apiUrl = this.rootUrl + '/orderMenuItemIdKeys';

   constructor(config: ApiConfiguration, http: HttpClient) {
     super(config, http);
   }

//     // Get orderMenuItemIdKey by order ID
//     getOrderMenuItemIdKeyByOrderId(orderId: number): Observable<OrderMenuItemResponse[]> {
//       return this.http.get<OrderMenuItemResponse[]>(`${this.apiUrl}/order/${orderId}`);
//     }
}
