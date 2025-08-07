import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderDialogComponent } from './order-dialog/order-dialog.component';

@NgModule({
  declarations: [OrderDialogComponent],
  imports: [CommonModule],
  exports: [OrderDialogComponent]
})
export class SharedModule { }