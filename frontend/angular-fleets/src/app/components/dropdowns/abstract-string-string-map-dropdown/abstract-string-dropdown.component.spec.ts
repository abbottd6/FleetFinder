import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';

import { AbstractStringDropdownComponent } from './abstract-string-dropdown.component';

describe('AbstractStringStringMapDropdownComponent', () => {
  let component: AbstractStringDropdownComponent;
  let fixture: ComponentFixture<AbstractStringDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AbstractStringDropdownComponent, ReactiveFormsModule, NgSelectModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(AbstractStringDropdownComponent);
    component = fixture.componentInstance;
    component.abstractControl = new FormControl(null);
    component.options = ['option1', 'option2'];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('options input binds correctly', () => {
    expect(component.options.length).toBe(2);
  });
});
