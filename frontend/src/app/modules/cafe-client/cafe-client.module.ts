import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CafeClientRoutingModule } from './cafe-client-routing.module';
import { MainComponent } from './pages/main/main.component';
import { MenuComponent } from './components/menu/menu.component';
import { MenuCafeComponent } from './pages/menu-cafe/menu-cafe.component';
import { OrderComponent } from './pages/order/order.component';
import { OrderHistoryComponent } from './pages/order-history/order-history.component';
import { ProfileComponent } from './pages/profile/profile.component';

@NgModule({
  declarations: [
    MainComponent,
    MenuComponent,
    MenuCafeComponent,
    OrderComponent,
    OrderHistoryComponent,
    ProfileComponent],
  imports: [
    CommonModule,
    CafeClientRoutingModule,
    FormsModule
  ]
})
export class CafeClientModule { }
