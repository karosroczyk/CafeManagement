import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuCafeComponent } from './menu-cafe.component';

describe('MenuCafeComponent', () => {
  let component: MenuCafeComponent;
  let fixture: ComponentFixture<MenuCafeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MenuCafeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MenuCafeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
