export interface OrderMenuItemKey {
  orderId: number;
  menuItemId: number;
  quantity: number;
}

export interface OrderMenuItemResponse {
  orderMenuItemIdKey: OrderMenuItemKey;
}