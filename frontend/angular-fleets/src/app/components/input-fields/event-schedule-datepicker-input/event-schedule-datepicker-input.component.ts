import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-event-schedule-datepicker-input',
  standalone: false,

  templateUrl: './event-schedule-datepicker-input.component.html',
  styleUrl: './event-schedule-datepicker-input.component.css'
})
export class EventScheduleDatepickerInputComponent implements OnInit{
  @Input() parentForm!: FormGroup;


  //subscribing to groupStatus dropdown for enable/disable of event date when groupStatus value changes
  //event date should only be enabled when groupStatus is set to "future/scheduled"
  ngOnInit() {
    this.parentForm.get('groupStatus')?.valueChanges.subscribe(value => {
      if (value == 2) {
        this.parentForm.get('eventScheduleDate')?.enable();
      }
      else {
        this.parentForm.get('eventScheduleDate')?.reset();
        this.parentForm.get('eventScheduleDate')?.disable();
      }
    })
  }
}
