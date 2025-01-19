import {Component, Input, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";

@Component({
  selector: 'app-schedule-time-dropdown',
  standalone: false,

  templateUrl: './schedule-time-dropdown.component.html',
  styleUrl: './schedule-time-dropdown.component.css'
})
export class ScheduleTimeDropdownComponent implements OnInit{
  @Input() parentForm!: FormGroup;
  timeOptions: string[] = [];

  ngOnInit(): void {
    this.generateTimeOptions();

    //subscribing to groupStatus dropdown for enable/disable of event time when groupStatus value changes
    //event time should only be enabled when groupStatus is set to "future/scheduled"
    this.parentForm.get('groupStatus')?.valueChanges.subscribe(value => {
      if (value == 2) {
        this.parentForm.get('eventScheduleTime')?.enable();
      } else {
        this.parentForm.get('eventScheduleTime')?.reset();
        this.parentForm.get('eventScheduleTime')?.disable();
      }
    });
  }

  generateTimeOptions(): void {
    for (let hour = 0; hour < 24; hour++) {
      const paddedHour: string = hour.toString().padStart(2, '0');
      this.timeOptions.push(`${paddedHour}:00`);
      this.timeOptions.push(`${paddedHour}:30`);
    }

    this.timeOptions.push('24:00');
  }
}
