import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainComponent } from './pages/main/main.component';
import { MenuCafeComponent } from './pages/menu-cafe/menu-cafe.component';

const routes: Routes = [
  {
    path: '',
    component: MainComponent
  },
  {
    path: 'menucafe',
    component: MenuCafeComponent
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CafeClientRoutingModule { }
