import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ScheduleTimeZoneDropdownComponent } from './schedule-time-zone-dropdown.component';

describe('ScheduleTimeZoneDropdownComponent', () => {
  let component: ScheduleTimeZoneDropdownComponent;
  let fixture: ComponentFixture<ScheduleTimeZoneDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, NgSelectModule],
      declarations: [ScheduleTimeZoneDropdownComponent],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ScheduleTimeZoneDropdownComponent);
    component = fixture.componentInstance;
    component.eventScheduleZoneControl = new FormControl({ value: null, disabled: true });
    component.groupStatusControl = new FormControl(null);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have a non-empty timeZones array', () => {
    expect(component.timeZones.length).toBeGreaterThan(0);
  });

  it('should contain UTC as the first timezone option', () => {
    expect(component.timeZones[0].value).toBe('UTC');
  });

  it('should enable eventScheduleZoneControl when groupStatus value is 2', () => {
    component.groupStatusControl.setValue(2);
    expect(component.eventScheduleZoneControl.enabled).toBeTrue();
  });

  it('should disable and reset eventScheduleZoneControl when groupStatus value is not 2', () => {
    component.eventScheduleZoneControl.enable();
    component.groupStatusControl.setValue(1);
    expect(component.eventScheduleZoneControl.disabled).toBeTrue();
  });

  it('should set the timezone control value to a matching timezone', () => {
    component.eventScheduleZoneControl.enable();
    component.setTimeZone('America/New_York');
    expect(component.eventScheduleZoneControl.value).toBe('America/New_York');
  });

  it('should not set the timezone control value when no match is found', () => {
    component.eventScheduleZoneControl.enable();
    component.eventScheduleZoneControl.setValue(null);
    component.setTimeZone('Mars/OlympusMons');
    expect(component.eventScheduleZoneControl.value).toBeNull();
  });
});
