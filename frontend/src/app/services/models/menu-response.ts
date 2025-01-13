export interface MenuResponse {
  item_id?: number;
  name?: string;
  description?: string;
  price?: number;
  categoryId?: number;
  image?: Uint8Array;
  createdAt?: string;
  updatedAt?: string;
  quantity?: number; // Additional property
  available: boolean; // Additional property
}