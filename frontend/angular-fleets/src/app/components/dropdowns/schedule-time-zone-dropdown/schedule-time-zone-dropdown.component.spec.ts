import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduleTimeZoneDropdownComponent } from './schedule-time-zone-dropdown.component';

describe('ScheduleTimeZoneDropdownComponent', () => {
  let component: ScheduleTimeZoneDropdownComponent;
  let fixture: ComponentFixture<ScheduleTimeZoneDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ScheduleTimeZoneDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScheduleTimeZoneDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
