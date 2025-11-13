import { OrderItemResponse } from './order-item-response';

export interface OrderResponse {
  id: number;
  status: string;
  total_price: number;
  customerId: number;
  createdAt: string;
  updatedAt: string;
  orderItems?: OrderItemResponse[];
}