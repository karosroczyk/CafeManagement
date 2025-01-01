import { Component, OnInit } from '@angular/core';
import { MenuService } from '../../../../services/services/menu/menu.service';
import { CategoryService } from '../../../../services/services/category/category.service';
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
    private categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this.getAllCategories();
  }

  getAllCategories() {
    this.categoryService.getAllCategories().subscribe({
      next: (res) => {
        this.pageResponseCategoryResponse = res;

        // Fetch menu items for each category
        res.data.forEach((category: any) => {
          this.getMenuItemsByCategoryName(category.name);
        });
      }
    });
  }

  getMenuItemsByCategoryName(categoryName: string) {
    this.menuService.getMenuItemsByCategoryName(categoryName).subscribe({
      next: (res) => {
          this.pageResponseMenuResponse = res;
          console.log(res);
         this.menuItemsByCategory[categoryName] = res;
      }
    });
  }
}
