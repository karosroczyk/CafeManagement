import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from './pages/login/login.component';
import {RegisterComponent} from './pages/register/register.component';
import {ActivateAccountComponent} from './pages/activate-account/activate-account.component';
import {authGuard} from './services/guard/auth.guard';
import {roleGuard} from './services/guard/roles/roles.guard';

const routes: Routes = [  {
                            path: 'login',
                            component: LoginComponent
                          },
                          {
                            path: 'register',
                            component: RegisterComponent
                          },
                          {
                            path: 'activate-account',
                            component: ActivateAccountComponent
                          },
                          {
                             path: 'cafe-client',
                             loadChildren: () => import('./modules/cafe-client/cafe-client.module').then(m => m.CafeClientModule),
                             canActivate: [authGuard]
                          }
                       ];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
