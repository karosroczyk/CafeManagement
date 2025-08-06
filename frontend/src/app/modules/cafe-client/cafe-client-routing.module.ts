import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainComponent } from './pages/main/main.component';
import { MenuCafeComponent } from './pages/menu-cafe/menu-cafe.component';
import { OrderComponent } from './pages/order/order.component';
import { OrderHistoryComponent } from './pages/order-history/order-history.component';
import { ProfileComponent } from './pages/profile/profile.component';
import {roleGuard} from '../../services/guard/roles/roles.guard';
const routes: Routes = [
  {
    path: '',
    component: MainComponent
  },
 {
    path: 'menucafe',
    component: MenuCafeComponent,
    canActivate: [roleGuard],
    data: { role: ['ADMIN'] }
 },
  {
    path: 'order',
    component: OrderComponent
  },
  {
    path: 'orderHistory',
    component: OrderHistoryComponent
  },
  {
    path: 'profile',
    component: ProfileComponent
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CafeClientRoutingModule { }
