export interface MenuResponse {
  id?: number;
  name?: string;
  description?: string;
  price?: number;
  categoryId?: number;
  image?: Uint8Array;     // frontend-only field
  createdAt?: string;     // frontend-only field
  updatedAt?: string;     // frontend-only field
  quantity?: number;      // UI field, not from backend
  available?: boolean;    // UI field, computed later
}