import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {
  CustomNotificationViewModel
} from "../../../models/NotificationPrefAndCustomNotesModels/CustomNotificationViewModel";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {DatePipe} from "@angular/common";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {
  CustomNoteStateRequest, CustomNotificationService
} from "../../../services/facade-services/custom-notification-service/custom-notification.service";

@Component({
  selector: 'app-custom-notification-chip',
  standalone: true,
  templateUrl: './custom-notification-chip.component.html',
  imports: [
    MatSlideToggle,
    DatePipe,
    ReactiveFormsModule
  ],
  styleUrl: './custom-notification-chip.component.css'
})

export class CustomNotificationChipComponent implements OnInit, OnChanges {
  @Input() customNoteInput!: CustomNotificationViewModel;

  @Output() enabledState = new EventEmitter<CustomNoteStateRequest>();

  protected enabledCtrl!: FormControl<boolean>;

  constructor(protected customNoteSettings: CustomNotificationService) {}

  ngOnInit() {
    this.enabledCtrl = new FormControl(this.customNoteInput.enabled, {nonNullable: true});
  }

  ngOnChanges(changes: SimpleChanges) {
    this.enabledCtrl.setValue(this.customNoteInput.enabled);
  }

  emitEnabledState() {
    const state = this.enabledCtrl.value;
    const customNoteId = this.customNoteInput.customNoteId;

    const request: CustomNoteStateRequest = {
      idCustomNote: this.customNoteInput.customNoteId,
      enabledState: state
    }

    this.enabledState.emit(request);
  }
}
