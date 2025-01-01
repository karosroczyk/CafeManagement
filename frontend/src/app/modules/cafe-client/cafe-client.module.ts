import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CafeClientRoutingModule } from './cafe-client-routing.module';
import { MainComponent } from './pages/main/main.component';
import { MenuComponent } from './components/menu/menu.component';
import { MenuCafeComponent } from './pages/menu-cafe/menu-cafe.component';

@NgModule({
  declarations: [
    MainComponent,
    MenuComponent,
    MenuCafeComponent],
  imports: [
    CommonModule,
    CafeClientRoutingModule
  ]
})
export class CafeClientModule { }
