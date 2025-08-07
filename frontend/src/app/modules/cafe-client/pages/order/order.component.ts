import { Component, OnInit } from '@angular/core';
import { MenuService } from '../../../../services/services/menu/menu.service';
import { CategoryService } from '../../../../services/services/category/category.service';
import { OrderService } from '../../../../services/services/order/order.service';
import { InventoryService } from '../../../../services/services/inventory/inventory.service';
import { PageResponse } from '../../../../services/models/page-response';
import { MenuResponse } from '../../../../services/models/menu-response';
import { OrderResponse } from '../../../../services/models/order-response';
import { CategoryResponse } from '../../../../services/models/category-response';
import { MatDialog } from '@angular/material/dialog';
import { OrderDialogComponent } from '../../../../components/shared/order-dialog/order-dialog.component';

@Component({
  selector: 'app-menu-cafe',
  standalone: false,
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.scss']
})
export class OrderComponent implements OnInit {
  pageResponseMenuResponse: PageResponse<MenuResponse> = {};
  pageResponseCategoryResponse: PageResponse<CategoryResponse> = {};
  pageResponseOrderResponse: PageResponse<OrderResponse> = {};
  menuItemsByCategory: { [key: string]: any } = {};
  basket: MenuResponse[] = [];

  constructor(
    private menuService: MenuService,
    private categoryService: CategoryService,
    private inventoryService: InventoryService,
    private orderService: OrderService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.getAllCategories();
  }

  getAllCategories() {
    this.categoryService.getAllCategories().subscribe({
      next: (res) => {
        if (res && res.data) {
          this.pageResponseCategoryResponse = res;
          res.data.forEach((category: any) => {
            this.getMenuItemsByCategoryName(category.name);
          });
        } else {
          console.error('Category response data is undefined or empty.');
        }
      },
      error: (err) => {
        console.error('Error fetching categories:', err);
      }
    });
  }

getMenuItemsByCategoryName(categoryName: string) {
  this.menuService.getMenuItemsByCategoryName(categoryName).subscribe({
    next: (res) => {
      this.menuItemsByCategory[categoryName] = res;

      const menuItemIds = this.menuItemsByCategory[categoryName].data.map(
        (item: { item_id: number }) => item.item_id
      );
      const quantities = this.menuItemsByCategory[categoryName].data.map(
        (item: { quantity?: number }) => item.quantity || 0
      );
      this.updateMenuItemAvailability(categoryName, menuItemIds, quantities);
    },
    error: (err) => {
      console.error('Error fetching menu items:', err);
    },
  });
}

  updateMenuItemAvailability(
    categoryName: string,
    menuItemIds: number[],
    quantities: number[]
  ) {
    this.inventoryService.getInventoryItemAvailability(menuItemIds, quantities).subscribe(
      (response: boolean[]) => {
        this.menuItemsByCategory[categoryName].data.forEach(
          (menuItem: { available?: boolean, quantity?: number }, index: number) => {
            menuItem.available = response[index];
            menuItem.quantity = 0;
          }
        );
        console.log('Inventory availability updated successfully');
      },
      (error) => {
        console.error('Error checking inventory availability:', error);
      }
    );
  }

    addToBasket(menuItem: any) {
      if (menuItem.quantity === 0) {
        this.dialog.open(OrderDialogComponent, {
          data: {
            title: 'Cannot Add to Basket',
            message: 'Please select a amount greater than 0 before adding to the basket.',
          },
        });
        return;
      }

      this.basket.push(menuItem);
      this.inventoryService.getInventoryItemAvailability(
        [menuItem.item_id],
        [1 + menuItem.quantity]
      ).subscribe(
        (response: boolean[]) => {
          if (!response[0]) menuItem.available = false;
        },
        (error) => {
          console.error('Error checking inventory availability:', error);
        }
      );
    }

  removeFromBasket(index: number) {
    this.basket[index].available = true;
    this.basket[index].quantity = 0;
    this.basket.splice(index, 1);
  }

    incrementQuantity(menuItem: any): void {
        if (menuItem.quantity === undefined) menuItem.quantity = 0;
          this.inventoryService.getInventoryItemAvailability([menuItem.item_id], [menuItem.quantity+1]).subscribe(
            (response: boolean[]) => {
                if (!response[0]) menuItem.available = true;
                else menuItem.quantity++;
              },
                 error => {
                   console.error('Error checking inventory availability:', error);
              }
        );
    }

    decrementQuantity(menuItem: any): void {
      if (menuItem.quantity > 0) menuItem.quantity--;
    }

    placeOrder(): void {
      const menuItemIds = this.basket.map(item => item.item_id || 0);
      const quantitiesOfMenuItems = this.basket.map(item => item.quantity || 0);

      console.log(this.basket);

      // TODO: replace 5 with actual customerId
      this.orderService.placeOrder(5, menuItemIds, quantitiesOfMenuItems).subscribe(
        () => {
          this.dialog.open(OrderDialogComponent, {
            data: {
              title: 'Order Placed',
              message: 'Your order has been placed successfully!',
            },
          });
        },
        (error) => {
          const errorMessage = error?.error?.message || 'An unexpected error occurred.';
          this.dialog.open(OrderDialogComponent, {
            data: {
              title: 'Order Failed',
              message: errorMessage,
            },
          });
        }
      );
    }
}

