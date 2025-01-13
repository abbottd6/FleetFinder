import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventScheduleDatepickerInputComponent } from './event-schedule-datepicker-input.component';

describe('EventScheduleDatepickerInputComponent', () => {
  let component: EventScheduleDatepickerInputComponent;
  let fixture: ComponentFixture<EventScheduleDatepickerInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventScheduleDatepickerInputComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventScheduleDatepickerInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
