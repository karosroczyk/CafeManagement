import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CafehomeComponent } from './cafehome.component';

describe('CafehomeComponent', () => {
  let component: CafehomeComponent;
  let fixture: ComponentFixture<CafehomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CafehomeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CafehomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
