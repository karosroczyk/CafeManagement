import { Component, OnInit } from '@angular/core';
import { PageResponse } from '../../services/models/page-response';
import { InventoryResponse } from '../../services/models/inventory-response';
import { InventoryService } from '../../services/services/inventory/inventory.service';

@Component({
  selector: 'app-cafehome',
  standalone: false,

  templateUrl: './cafehome.component.html',
  styleUrl: './cafehome.component.scss'
})
export class CafehomeComponent implements OnInit{
  pageResponseInventoryResponse: PageResponse<InventoryResponse> = {};
  constructor(
    private inventoryService: InventoryService){
  }

  ngOnInit(): void{
    this.getAllInventoryItems();
  }

  getAllInventoryItems(){
    this.inventoryService.getAllInventoryItems().subscribe({
      next: (res) => {
      this.pageResponseInventoryResponse = res;
      }});
  }

  logout(){
    localStorage.removeItem('token');
    window.location.reload();
  }
}
