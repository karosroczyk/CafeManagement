import { Component, OnInit } from '@angular/core';
import { MenuService } from '../../../../services/services/menu/menu.service';
import { OrderService } from '../../../../services/services/order/order.service';
import { OrderMenuItemService } from '../../../../services/services/orderMenuItem/order-menu-item.service';
import { UserService } from '../../../../services/services/user/user.service';
import { PageResponse } from '../../../../services/models/page-response';
import { InventoryResponse } from '../../../../services/models/inventory-response';
import { OrderResponse } from '../../../../services/models/order-response';
import { MenuResponse } from '../../../../services/models/menu-response';
import { OrderItemResponse } from '../../../../services/models/order-item-response';

@Component({
  selector: 'app-order-history',
  standalone: false,
  
  templateUrl: './order-history.component.html',
  styleUrl: './order-history.component.scss'
})
export class OrderHistoryComponent implements OnInit{
    pageResponseInventoryResponse: PageResponse<InventoryResponse> = {};
    pageResponseOrderResponse: OrderResponse[] = [];
    orderItemsMap: { [orderId: number]: OrderItemResponse[] } = {};
    menuItemNameMap: { [menuItemId: number]: MenuResponse } = {};

    constructor(
      private menuService: MenuService,
      private orderService: OrderService,
      private userService: UserService,
      private orderMenuItemService: OrderMenuItemService){
    }

    ngOnInit(): void{
        this.fetchOrders();
    }

    fetchOrders() {
        this.userService.getCurrentUser().subscribe({
            next: (user) => {
                if (user && user.id !== undefined) {
                        const userId = user.id;
                        this.orderService.getOrdersByCustomerId(userId).subscribe({
                        next: (res) => {
                            this.pageResponseOrderResponse = res;
                    }});
                } else {
                    console.error('User not found');
                }
            },
            error: (err) => {
                console.error('Error fetching user', err);
            }
        });
    }

    getOrderMenuItemIdKeyByOrderId(order: OrderResponse) {
        if (this.orderItemsMap[order.id]) {
            return;
        }

        if (order.orderItems && order.orderItems.length > 0) {
            this.orderItemsMap[order.id] = order.orderItems;
            order.orderItems.forEach(item => this.getMenuItemById(item.menuItemId));
        } else {
            console.log('No order items found for this order');
            this.orderItemsMap[order.id] = [];
        }
    }

    getMenuItemById(menuItemId: number) {
        if (this.menuItemNameMap[menuItemId]) return;

        this.menuService.getMenuItemById(menuItemId).subscribe({
            next: (res) => {
                this.menuItemNameMap[menuItemId] = res;
            },
            error: (err) => {
                console.error(`Error fetching menu item ${menuItemId}:`, err);
            }
        });
    }
}
