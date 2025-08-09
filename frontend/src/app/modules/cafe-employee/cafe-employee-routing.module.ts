import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CafeEmployeeMainPage } from './pages/main/main.component';
import { MenuCafeComponent } from './pages/menu-cafe/menu-cafe.component';
import { OrderHistoryComponent } from './pages/order-history/order-history.component';
import { ProfileComponent } from './pages/profile/profile.component';
import {roleGuard} from '../../services/guard/roles/roles.guard';
const routes: Routes = [
  {
    path: '',
    component: CafeEmployeeMainPage,
   canActivate: [roleGuard],
   data: { role: ['EMPLOYEE'] }
   },
    {
       path: 'menucafe',
       component: MenuCafeComponent,
       canActivate: [roleGuard],
       data: { role: ['EMPLOYEE'] }
    },
     {
       path: 'orders',
       component: OrderHistoryComponent,
       canActivate: [roleGuard],
       data: { role: ['EMPLOYEE'] }
     },
  {
    path: 'profile',
    component: ProfileComponent,
   canActivate: [roleGuard],
   data: { role: ['EMPLOYEE'] }
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CafeEmployeeRoutingModule { }
