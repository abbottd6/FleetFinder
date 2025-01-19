import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduleTimeDropdownComponent } from './schedule-time-dropdown.component';

describe('ScheduleTimeDropdownComponent', () => {
  let component: ScheduleTimeDropdownComponent;
  let fixture: ComponentFixture<ScheduleTimeDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ScheduleTimeDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScheduleTimeDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
