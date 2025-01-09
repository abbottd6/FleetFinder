import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventscheduleDatepickerComponent } from './eventschedule-datepicker.component';

describe('EventscheduleDatepickerComponent', () => {
  let component: EventscheduleDatepickerComponent;
  let fixture: ComponentFixture<EventscheduleDatepickerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventscheduleDatepickerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventscheduleDatepickerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
