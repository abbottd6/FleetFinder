import {Component, OnDestroy, OnInit} from '@angular/core';
import {map, Observable, Subject, takeUntil} from "rxjs";
import {UserService} from "../../services/user-services/user.service";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatLabel} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {UpdateNotificationPreferenceRequest} from "../../models/NotificationPrefAndCustomNotesModels/update-notification-preference-request";
import {NotificationApiService} from "../../services/api-services/notification-api/notification-api.service";
import {MatExpansionModule, MatExpansionPanelTitle} from "@angular/material/expansion";
import {NgIf} from "@angular/common";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {
  CustomNotificationFormComponent
} from "../user-profile-notification-forms/custom-notification-form/custom-notification-form.component";
import {
  NotificationSettingsApiService
} from "../../services/api-services/notification-api/notification-settings-api.service";
import {
  CustomNotificationViewModel
} from "../../models/NotificationPrefAndCustomNotesModels/CustomNotificationViewModel";
import {Page} from "../../services/api-services/group-listings-fetch-api/group-listing-fetch.service";
import {environment} from "../../../environments/environment";
import {CustomNotificationChipComponent} from "./custom-notification-chip/custom-notification-chip.component";

@Component({
  selector: 'app-profile-notifications-tab',
  standalone: true,
  templateUrl: './profile-notifications-tab.component.html',
  imports: [
    MatSlideToggle,
    MatLabel,
    MatIcon,
    ReactiveFormsModule,
    MatExpansionModule,
    MatExpansionPanelTitle,
    NgIf,
    CustomNotificationFormComponent,
    CustomNotificationChipComponent
  ],
  styleUrl: './profile-notifications-tab.component.css'
})
export class ProfileNotificationsTabComponent implements OnDestroy {
  private destroy$ = new Subject<void>();

  public hasDiscordAcct!: boolean;
  public doNotShowCustomNotesForm: boolean = true;

  public discSysNotesControl: FormControl<boolean> = new FormControl();
  public discSysNotesSaved: boolean = false;
  public discSysNotesSaveError: boolean = false;

  public discGroupNotesControl: FormControl<boolean> = new FormControl();
  discGroupNotesSaved: boolean = false;
  discGroupNotesSaveError: boolean = false;

  public discSocialNotesControl: FormControl<boolean> = new FormControl();
  discSocialNotesSaved: boolean = false;
  discSocialNotesSaveError: boolean = false;

  protected userCustomNotes: CustomNotificationViewModel[] = [];
  protected noCustomNotes: boolean = true;

  constructor(protected userService: UserService, private noteSettingsApiService: NotificationSettingsApiService) {
    this.getUserNotePrefs();
    this.getMyCustomNotifications();

    this.userService.sessionUser$.pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.hasDiscordAcct = user?.discordUsername != null;

        if(!this.hasDiscordAcct) {
          this.discSysNotesControl.disable();
          this.discGroupNotesControl.disable();
          this.discSocialNotesControl.disable();
        }
        else {
          this.discSysNotesControl.enable();
          this.discGroupNotesControl.enable();
          this.discSocialNotesControl.enable();
        }
      })
  }

  showCustomNoteForm(){
    this.doNotShowCustomNotesForm = !this.doNotShowCustomNotesForm;
  }

  createOrUpdateSuccess(val: boolean) {
    this.doNotShowCustomNotesForm = val;
  }

  getUserNotePrefs() {
    this.discSysNotesControl.setValue(this.userService.sysNotesEnabled ?? false);
    this.discGroupNotesControl.setValue(this.userService.groupNotesEnabled ?? false);
    this.discSocialNotesControl.setValue(this.userService.socialNotesEnabled ?? false);
  }

  getMyCustomNotifications() {
    const IDX: number = 0;
    const PAGE: number = 20;

    this.noteSettingsApiService.getMyCustomNotifications(IDX, PAGE).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (page: Page<CustomNotificationViewModel>) => {
          if(!environment.production) {
            console.log('custom notes logged: ', page.content);
          }
          this.userCustomNotes = page.content;
        },
        error: (err) => {
          console.error('Error fetching user\'s custom notifications', err);
        },
        complete: () => {
          this.noCustomNotes = (this.userCustomNotes.length === 0);
        }
      }
    )
  }

  updateDiscordSysNotesPref() {
    const label: string = 'sysNotes';
    const val = this.discSysNotesControl.value;

    const updateRequest = new UpdateNotificationPreferenceRequest(label, val);

    this.noteSettingsApiService.updateExternalNotificationPreference(updateRequest).subscribe(
      response => {
        this.userService.refreshUser();
        if(response === this.discSysNotesControl.value) {
          this.discSysNotesSaved = true;
          setTimeout(() => this.discSysNotesSaved = false, 2000);
        }
        else {
          this.discSysNotesSaveError = true;
          setTimeout(() => this.discSysNotesSaved = false, 2000);
          this.getUserNotePrefs();
        }
      }
    );
  }

  updateDiscordGroupNotesPref() {
    const label: string = 'groupNotes';
    const val = this.discGroupNotesControl.value;

    const updateRequest = new UpdateNotificationPreferenceRequest(label, val);

    this.noteSettingsApiService.updateExternalNotificationPreference(updateRequest).subscribe(
      response => {
        this.userService.refreshUser();
        if(response === this.discGroupNotesControl.value) {
          this.discGroupNotesSaved = true;
          setTimeout(() => this.discGroupNotesSaved = false, 2000);
        }
        else {
          this.discGroupNotesSaveError = true;
          setTimeout(() => this.discGroupNotesSaved = false, 2000);
          this.getUserNotePrefs();
        }
      }
    )
  }

  updateDiscordSocialNotesPref() {
    const label: string = 'socialNotes';
    const val = this.discSocialNotesControl.value;

    const updateRequest = new UpdateNotificationPreferenceRequest(label, val);

    this.noteSettingsApiService.updateExternalNotificationPreference(updateRequest).subscribe(
      response => {
        this.userService.refreshUser();
        if(response === this.discSocialNotesControl.value) {
          this.discSocialNotesSaved = true;
          setTimeout(() => this.discSocialNotesSaved = false, 2000);
        }
        else {
          this.discSocialNotesSaveError = true;
          setTimeout(() => this.discSocialNotesSaved = false, 2000);
          this.getUserNotePrefs();
        }
      }
    )
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
