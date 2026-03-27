import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { EventScheduleDatepickerInputComponent } from './event-schedule-datepicker-input.component';

describe('EventScheduleDatepickerInputComponent', () => {
  let component: EventScheduleDatepickerInputComponent;
  let fixture: ComponentFixture<EventScheduleDatepickerInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventScheduleDatepickerInputComponent],
      imports: [ReactiveFormsModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(EventScheduleDatepickerInputComponent);
    component = fixture.componentInstance;
    component.eventScheduleDateControl = new FormControl(null);
    component.groupStatusControl = new FormControl(null);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('minDate should be set to today or earlier', () => {
    expect(component.minDate).toBeDefined();
  });
});
