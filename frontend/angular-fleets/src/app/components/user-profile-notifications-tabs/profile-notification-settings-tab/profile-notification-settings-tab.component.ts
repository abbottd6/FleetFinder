import {Component, OnDestroy, OnInit} from '@angular/core';
import {map, Subject, takeUntil} from "rxjs";
import {UserService} from "../../../services/user-services/user.service";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatLabel} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {UpdateNotificationPreferenceRequest} from "../../../models/private-user/update-notification-preference-request";
import {NotificationApiService} from "../../../services/api-services/notification-api/notification-api.service";

@Component({
  selector: 'app-profile-notification-settings-tab',
  standalone: true,
  templateUrl: './profile-notification-settings-tab.component.html',
  imports: [
    MatSlideToggle,
    MatLabel,
    MatIcon,
    ReactiveFormsModule
  ],
  styleUrl: './profile-notification-settings-tab.component.css'
})
export class ProfileNotificationSettingsTabComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  public sysNotesControl: FormControl<boolean> = new FormControl();
  public sysDisabled: boolean = false;
  public groupNotesControl: FormControl<boolean> = new FormControl();
  groupDisabled: boolean = false;
  public socialNotesControl: FormControl<boolean> = new FormControl();
  socialDisabled: boolean = false;

  constructor(private userService: UserService, private notificationApiService: NotificationApiService) {
    this.getUserNotePrefs();
  }

  ngOnInit() {

  }

  getUserNotePrefs() {
    this.sysNotesControl.setValue(this.userService.sysNotesEnabled ?? false);
    this.groupNotesControl.setValue(this.userService.groupNotesEnabled ?? false);
    this.socialNotesControl.setValue(this.userService.socialNotesEnabled ?? false);
  }

  updateDiscordSysNotesPref() {
    const label: string = 'sysNotes';
    const val = this.sysNotesControl.value;

    const updateRequest = new UpdateNotificationPreferenceRequest(label, val);


    this.notificationApiService.updateExternalNotificationPreference(updateRequest).subscribe(
      response => {
        console.log(response);
        this.userService.refreshUser();
        this.getUserNotePrefs();
        setTimeout(() => this.sysDisabled = false, 500);
      }
    );
  }

  updateDiscordGroupNotesPref() {
    const label: string = 'groupNotes';
    const val = this.groupNotesControl.value;

    const updateRequest = new UpdateNotificationPreferenceRequest(label, val);

    this.notificationApiService.updateExternalNotificationPreference(updateRequest).subscribe(
      response => {
        this.groupDisabled = true;
        console.log(response);
        this.userService.refreshUser();
        this.getUserNotePrefs();
        setTimeout(() => this.groupDisabled = false, 500);
      }
    )
  }

  updateDiscordSocialNotesPref() {
    const label: string = 'socialNotes';
    const val = this.socialNotesControl.value;

    const updateRequest = new UpdateNotificationPreferenceRequest(label, val);

    this.notificationApiService.updateExternalNotificationPreference(updateRequest).subscribe(
      response => {
        this.socialDisabled = true;
        console.log(response);
        this.userService.refreshUser();
        this.getUserNotePrefs();
        setTimeout(() => this.socialDisabled = false, 500);
      }
    )
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
