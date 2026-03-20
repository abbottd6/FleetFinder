import {Component, Input} from '@angular/core';
import {
  CustomNotificationViewModel
} from "../../../models/NotificationPrefAndCustomNotesModels/CustomNotificationViewModel";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {DatePipe} from "@angular/common";

@Component({
  selector: 'app-custom-notification-chip',
  standalone: true,
  templateUrl: './custom-notification-chip.component.html',
  imports: [
    MatSlideToggle,
    DatePipe
  ],
  styleUrl: './custom-notification-chip.component.css'
})
export class CustomNotificationChipComponent {
  @Input() customNoteInput!: CustomNotificationViewModel;

  constructor() {}


}
