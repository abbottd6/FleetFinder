import {Component, Input} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-event-schedule-datepicker-input',
  standalone: false,

  templateUrl: './event-schedule-datepicker-input.component.html',
  styleUrl: './event-schedule-datepicker-input.component.css'
})
export class EventScheduleDatepickerInputComponent {
  @Input() eventDateControl!: FormGroup;
}
