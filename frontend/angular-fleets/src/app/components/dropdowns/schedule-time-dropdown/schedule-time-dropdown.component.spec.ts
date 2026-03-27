import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ScheduleTimeDropdownComponent } from './schedule-time-dropdown.component';

describe('ScheduleTimeDropdownComponent', () => {
  let component: ScheduleTimeDropdownComponent;
  let fixture: ComponentFixture<ScheduleTimeDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, NgSelectModule],
      declarations: [ScheduleTimeDropdownComponent],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ScheduleTimeDropdownComponent);
    component = fixture.componentInstance;
    component.eventScheduleTimeControl = new FormControl({ value: null, disabled: true });
    component.groupStatusControl = new FormControl(null);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should generate 48 time options covering all half-hours in a day', () => {
    expect(component.timeOptions.length).toBe(48);
    expect(component.timeOptions[0]).toBe('00:00');
    expect(component.timeOptions[1]).toBe('00:30');
    expect(component.timeOptions[46]).toBe('23:00');
    expect(component.timeOptions[47]).toBe('23:30');
  });

  it('should enable eventScheduleTimeControl when groupStatus value is 2', () => {
    component.groupStatusControl.setValue(2);
    expect(component.eventScheduleTimeControl.enabled).toBeTrue();
  });

  it('should disable and reset eventScheduleTimeControl when groupStatus value is not 2', () => {
    component.eventScheduleTimeControl.enable();
    component.groupStatusControl.setValue(1);
    expect(component.eventScheduleTimeControl.disabled).toBeTrue();
  });
});
