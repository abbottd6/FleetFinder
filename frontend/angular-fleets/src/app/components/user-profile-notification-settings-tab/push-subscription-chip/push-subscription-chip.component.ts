import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Subject, takeUntil} from 'rxjs';
import {PushSubViewModel} from "../../../models/NotificationPrefAndCustomNotesModels/PushSubViewModel";
import {DatePipe, NgIf} from "@angular/common";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatIcon} from "@angular/material/icon";
import {MatLabel} from "@angular/material/input";
import {MatTooltip} from "@angular/material/tooltip";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {
  NotificationSettingsApiService
} from "../../../services/api-services/notification-api/notification-settings-api.service";
import {HttpErrorResponse} from "@angular/common/http";

export interface PushSubPrefChangeRequest {
  idPushSub: number,
  label: string,
  value: boolean
}

type TimeoutObject = ReturnType<typeof setTimeout> | null;

@Component({
  selector: 'app-push-subscription-chip',
  standalone: true,
  templateUrl: './push-subscription-chip.component.html',
  imports: [
    DatePipe,
    MatIcon,
    MatLabel,
    MatSlideToggle,
    MatTooltip,
    NgIf,
    ReactiveFormsModule
  ],
  styleUrl: './push-subscription-chip.component.css'
})
export class PushSubscriptionChipComponent implements OnDestroy, OnInit {
  private destroy$ = new Subject<void>
  @Input() inputSub!: PushSubViewModel;
  @Output() subStateChangeSuccess = new EventEmitter<PushSubViewModel>();
  @Output() subStateChangeFailure = new EventEmitter<HttpErrorResponse>();
  @Output() deleteThisPushSub = new EventEmitter<PushSubViewModel>;

  public inputSubSysNotesControl: FormControl<boolean> = new FormControl();

  public inputSubGroupNotesControl: FormControl<boolean> = new FormControl();

  public inputSubSocialNotesControl: FormControl<boolean> = new FormControl();

  inputSubPrefsSaved: boolean = false;
  inputSubPrefsSaveError: boolean = false;

  savedMessageTimer: TimeoutObject = null;

  constructor(private noteSettingsApiService: NotificationSettingsApiService){
  }

  ngOnInit() {
    this.inputSubSysNotesControl.setValue(this.inputSub.sysNotesEnabled);
    this.inputSubGroupNotesControl.setValue(this.inputSub.groupNotesEnabled);
    this.inputSubSocialNotesControl.setValue(this.inputSub.socialNotesEnabled);
  }

  updateInputSubSysNotePref() {
    const updateRequest: PushSubPrefChangeRequest = {
      idPushSub: this.inputSub.idPushSub,
      label: 'sysNotes',
      value: this.inputSubSysNotesControl.value,
    }

    this.sendStateChangeRequest(updateRequest);
  }

  updateInputSubGroupNotesPref() {
    const updateRequest: PushSubPrefChangeRequest = {
      idPushSub: this.inputSub.idPushSub,
      label: 'groupNotes',
      value: this.inputSubGroupNotesControl.value,
    }

    this.sendStateChangeRequest(updateRequest)
  }

  updateInputSubSocialNotesPref() {
      const updateRequest: PushSubPrefChangeRequest = {
        idPushSub: this.inputSub.idPushSub,
        label: 'socialNotes',
        value: this.inputSubSocialNotesControl.value,
      }

      this.sendStateChangeRequest(updateRequest);
  }

  sendStateChangeRequest(updateRequest: PushSubPrefChangeRequest) {

    this.noteSettingsApiService.updatePushSubNotificationPreference(updateRequest).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: data => {
          this.inputSubPrefsSaved = true;
          this.subStateChangeSuccess.emit(data);

          if (this.savedMessageTimer) {
            clearTimeout(this.savedMessageTimer);
          }

          this.savedMessageTimer = setTimeout(() => {
            this.inputSubPrefsSaved = false;
            this.savedMessageTimer = null;
          }, 3000);
        },
        error: (err) => {
          this.subStateChangeFailure.emit(err as HttpErrorResponse);
        }
      })
  }

  deletePushSubscription() {
    this.deleteThisPushSub.emit(this.inputSub);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
