import { TestBed } from '@angular/core/testing';

import { OrderMenuItemService } from './order-menu-item.service';

describe('OrderMenuItemService', () => {
  let service: OrderMenuItemService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrderMenuItemService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
