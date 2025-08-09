import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CafeEmployeeRoutingModule } from './cafe-employee-routing.module';
import { CafeEmployeeMainPage } from './pages/main/main.component';
import { SharedModule } from '../../components/shared/shared.module';
import { MenuComponent } from './components/menu/menu.component';
import { MenuCafeComponent } from './pages/menu-cafe/menu-cafe.component';
import { OrderHistoryComponent } from './pages/order-history/order-history.component';
import { ProfileComponent } from './pages/profile/profile.component';

@NgModule({
  declarations: [
      MenuComponent,
      CafeEmployeeMainPage,
      MenuCafeComponent,
      OrderHistoryComponent,
      ProfileComponent],
  imports: [
    CommonModule,
    CafeEmployeeRoutingModule,
    FormsModule,
    SharedModule
  ]
})
export class CafeEmployeeModule { }
