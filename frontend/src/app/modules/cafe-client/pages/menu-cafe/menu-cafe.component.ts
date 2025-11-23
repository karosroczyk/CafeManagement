import { Component, OnInit } from '@angular/core';
import { MenuService } from '../../../../services/services/menu/menu.service';
import { CategoryService } from '../../../../services/services/category/category.service';
import { AuthService } from '../../../../services/auth/auth.service';
import { PageResponse } from '../../../../services/models/page-response';
import { MenuResponse } from '../../../../services/models/menu-response';
import { CategoryResponse } from '../../../../services/models/category-response';

@Component({
  selector: 'app-menu-cafe',
  standalone: false,
  templateUrl: './menu-cafe.component.html',
  styleUrls: ['./menu-cafe.component.scss']
})
export class MenuCafeComponent implements OnInit {
  pageResponseMenuResponse: PageResponse<MenuResponse> = {};
  pageResponseCategoryResponse: PageResponse<CategoryResponse> = {};
  menuItemsByCategory: { [key: string]: any } = {}; // To store menu items by category

  constructor(
    private menuService: MenuService,
    private categoryService: CategoryService,
    public authService: AuthService
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

    },
    error: (err) => {
      console.error('Error fetching menu items:', err);
    },
  });
}
}
