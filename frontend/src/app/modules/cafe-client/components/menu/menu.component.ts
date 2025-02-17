import { Component, OnInit } from '@angular/core';
import { MenuService } from '../../../../services/services/menu/menu.service';
import { PageResponse } from '../../../../services/models/page-response';
import { MenuResponse } from '../../../../services/models/menu-response';
import { Router } from "@angular/router";

@Component({
  selector: 'app-menu',
  standalone: false,

  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit {
  pageResponseMenuResponse: PageResponse<MenuResponse> = {};
  constructor(
    private router: Router,
    private menuService: MenuService){
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
    this.router.navigate(['login']);
  }

  getAllMenuItems(){
    this.menuService.getAllMenuItems().subscribe({
      next: (res) => {
      this.pageResponseMenuResponse = res;
      }});
  }

}
