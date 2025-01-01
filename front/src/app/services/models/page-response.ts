import { CategoryResponse } from '../models/category-response';
export interface PageResponse<T> {
  data?: Array<T>;
  currentPage?: number;
  totalPages?: number;
  totalElements?: number;
  size?: number;
}
