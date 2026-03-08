import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AbstractDropdownComponent } from './abstract-dropdown.component';

describe('AbstractDropdownComponent', () => {
  let component: AbstractDropdownComponent;
  let fixture: ComponentFixture<AbstractDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AbstractDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AbstractDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
