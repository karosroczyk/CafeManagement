import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CafeClientMainPage } from './pages/main/main.component';
import { MenuCafeComponent } from './pages/menu-cafe/menu-cafe.component';
import { OrderComponent } from './pages/order/order.component';
import { OrderHistoryComponent } from './pages/order-history/order-history.component';
import { ProfileComponent } from './pages/profile/profile.component';
import {roleGuard} from '../../services/guard/roles/roles.guard';
const routes: Routes = [
  {
    path: '',
    component: CafeClientMainPage,
    canActivate: [roleGuard],
    data: { role: ['CLIENT'] }
  },
 {
    path: 'menucafe',
    component: MenuCafeComponent,
    canActivate: [roleGuard],
    data: { role: ['CLIENT'] }
 },
  {
    path: 'order',
    component: OrderComponent,
     canActivate: [roleGuard],
     data: { role: ['CLIENT'] }
  },
  {
    path: 'orderHistory',
    component: OrderHistoryComponent,
    canActivate: [roleGuard],
    data: { role: ['CLIENT'] }
  },
  {
    path: 'profile',
    component: ProfileComponent,
   canActivate: [roleGuard],
   data: { role: ['CLIENT'] }
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CafeClientRoutingModule { }
