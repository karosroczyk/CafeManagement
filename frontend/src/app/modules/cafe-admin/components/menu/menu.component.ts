import { Component, OnInit } from '@angular/core';
import { MenuService } from '../../../../services/services/menu/menu.service';
import { AuthService } from '../../../../services/auth/auth.service';
import { PageResponse } from '../../../../services/models/page-response';
import { MenuResponse } from '../../../../services/models/menu-response';

@Component({
  selector: 'app-menu',
  standalone: false,

  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit {
  pageResponseMenuResponse: PageResponse<MenuResponse> = {};
  constructor(
    private menuService: MenuService,
    private authService: AuthService){
  }

  ngOnInit(): void {
    const linkColor = document.querySelectorAll('.nav-link');
    linkColor.forEach(link => {
      if (window.location.href.endsWith(link.getAttribute('href') || '')) {
        link.classList.add('active');
      }
      link.addEventListener('click', () => {
        linkColor.forEach(l => l.classList.remove('active'));
        link.classList.add('active');
      });
    });

    this.getAllMenuItems();
  }

  logout(){
    this.authService.logout();
  }

  getAllMenuItems(){
    this.menuService.getAllMenuItems().subscribe({
      next: (res) => {
      this.pageResponseMenuResponse = res;
      }});
  }

}
