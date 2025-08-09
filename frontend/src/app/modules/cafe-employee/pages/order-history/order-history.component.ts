import { Component, OnInit } from '@angular/core';
import { MenuService } from '../../../../services/services/menu/menu.service';
import { OrderService } from '../../../../services/services/order/order.service';
import { OrderMenuItemService } from '../../../../services/services/orderMenuItem/order-menu-item.service';
import { PageResponse } from '../../../../services/models/page-response';
import { InventoryResponse } from '../../../../services/models/inventory-response';
import { OrderResponse } from '../../../../services/models/order-response';
import { MenuResponse } from '../../../../services/models/menu-response';
import { OrderMenuItemResponse, OrderMenuItemKey } from '../../../../services/models/order-menuitem-response';

@Component({
  selector: 'app-order-history',
  standalone: false,
  
  templateUrl: './order-history.component.html',
  styleUrl: './order-history.component.scss'
})
export class OrderHistoryComponent implements OnInit{
    pageResponseInventoryResponse: PageResponse<InventoryResponse> = {};
    pageResponseOrderResponse: OrderResponse[] = [];
    orderMenuItemsMap: { [orderId: number]: OrderMenuItemKey[] } = {};
    menuItemNameMap: { [menuItemId: number]: MenuResponse } = {};

    constructor(
      private menuService: MenuService,
      private orderService: OrderService,
      private orderMenuItemService: OrderMenuItemService){
    }

    ngOnInit(): void{
      this.fetchOrders();
    }
      fetchOrders() {
        this.orderService.getOrdersByCustomerId(5).subscribe({
          next: (res) => {
            this.pageResponseOrderResponse = res;
          }});
      }

    getOrderMenuItemIdKeyByOrderId(orderId: number){
      this.orderMenuItemService.getOrderMenuItemIdKeyByOrderId(orderId).subscribe({
        next: (res: OrderMenuItemResponse[]) => {
        this.orderMenuItemsMap[orderId] = res.map(item => item.orderMenuItemIdKey);

      this.orderMenuItemsMap[orderId].forEach(item => {
        this.getMenuItemById(item.menuItemId);
      });
        }});
    }

    getMenuItemById(menuItemId: number) {
      if (this.menuItemNameMap[menuItemId]) {
        return;
      }

      this.menuService.getMenuItemById(menuItemId).subscribe({
        next: (res) => {
          this.menuItemNameMap[menuItemId] = res;
        }});
    }
}
