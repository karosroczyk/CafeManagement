import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderDialogComponent } from './order-dialog/order-dialog.component';
import { RouterModule } from '@angular/router';
import { MainComponent } from './main/main.component';

@NgModule({
  declarations: [OrderDialogComponent,
    MainComponent],
  imports: [CommonModule,
    RouterModule],
  exports: [OrderDialogComponent,
    MainComponent]
})
export class SharedModule { }