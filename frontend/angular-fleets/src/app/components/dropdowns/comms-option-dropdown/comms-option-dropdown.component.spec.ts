import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { CommsOptionDropdownComponent } from './comms-option-dropdown.component';

describe('CommsOptionDropdownComponent', () => {
  let component: CommsOptionDropdownComponent;
  let fixture: ComponentFixture<CommsOptionDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, NgSelectModule],
      declarations: [CommsOptionDropdownComponent],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CommsOptionDropdownComponent);
    component = fixture.componentInstance;
    component.commsOptionControl = new FormControl(null);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have a non-empty comms options array', () => {
    expect(component.commsOptions.length).toBeGreaterThan(0);
  });

  it('should contain expected comms option values', () => {
    expect(component.commsOptions).toContain('Required');
    expect(component.commsOptions).toContain('Optional');
    expect(component.commsOptions).toContain('No Comms');
  });

  it('should accept a value via the commsOptionControl input', () => {
    component.commsOptionControl.setValue('Required');
    expect(component.commsOptionControl.value).toBe('Required');
  });
});
