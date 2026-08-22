import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduleRsvpPopupComponent } from './schedule-rsvp-popup.component';

describe('ScheduleRsvpPopupComponent', () => {
  let component: ScheduleRsvpPopupComponent;
  let fixture: ComponentFixture<ScheduleRsvpPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScheduleRsvpPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScheduleRsvpPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
