import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CafeAdminRoutingModule } from './cafe-admin-routing.module';
import { CafeAdminMainPage } from './pages/main/main.component';
import { SharedModule } from '../../components/shared/shared.module';
import { MenuComponent } from './components/menu/menu.component';
import { ProfileListComponent } from './pages/profile-list/profile-list.component';
import { ProfileComponent } from './pages/profile/profile.component';

@NgModule({
  declarations: [
      MenuComponent,
      CafeAdminMainPage,
      ProfileComponent,
      ProfileListComponent],
  imports: [
    CommonModule,
    CafeAdminRoutingModule,
    FormsModule,
    SharedModule
  ]
})
export class CafeAdminModule { }
