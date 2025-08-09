import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainComponent } from './pages/main/main.component';
import { ProfileComponent } from './pages/profile/profile.component';
import {roleGuard} from '../../services/guard/roles/roles.guard';
const routes: Routes = [
  {
    path: '',
    component: MainComponent,
    canActivate: [roleGuard],
    data: { role: ['ADMIN'] }
   },
  {
    path: 'profiles',
    component: ProfileComponent,
   canActivate: [roleGuard],
   data: { role: ['ADMIN'] }
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CafeAdminRoutingModule { }
