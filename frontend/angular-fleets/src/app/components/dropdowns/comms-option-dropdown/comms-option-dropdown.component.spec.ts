import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommsOptionDropdownComponent } from './comms-option-dropdown.component';

describe('CommsOptionDropdownComponent', () => {
  let component: CommsOptionDropdownComponent;
  let fixture: ComponentFixture<CommsOptionDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CommsOptionDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CommsOptionDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
