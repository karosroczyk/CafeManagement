import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CafeAdminMainPage } from './pages/main/main.component';
import { roleGuard } from '../../services/guard/roles/roles.guard';
import { ProfileListComponent } from './pages/profile-list/profile-list.component';
import { ProfileComponent } from './pages/profile/profile.component';

const routes: Routes = [
  {
    path: '',
    component: CafeAdminMainPage,
    canActivate: [roleGuard],
    data: { role: ['ADMIN'] }
   },
  {
    path: 'profiles',
    component: ProfileListComponent,
    canActivate: [roleGuard],
    data: { role: ['ADMIN'] }
  },
{
//    path: 'profile/:id',
    path: 'profile',
    component: ProfileComponent,
    canActivate: [roleGuard],
    data: { role: ['ADMIN'], prerender: false }
}
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CafeAdminRoutingModule { }
