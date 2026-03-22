import {Component, inject, OnDestroy} from '@angular/core';
import {map, shareReplay, Subject, takeUntil} from "rxjs";
import {UserService} from "../../services/user-services/user.service";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatLabel} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {UpdateNotificationPreferenceRequest} from "../../models/NotificationPrefAndCustomNotesModels/update-notification-preference-request";
import {MatExpansionModule, MatExpansionPanelTitle} from "@angular/material/expansion";
import {AsyncPipe, NgIf} from "@angular/common";
import {
  CustomNotificationFormComponent
} from "../user-profile-notification-forms/custom-notification-form/custom-notification-form.component";
import {
  NotificationSettingsApiService
} from "../../services/api-services/notification-api/notification-settings-api.service";
import {
  CustomNotificationViewModel
} from "../../models/NotificationPrefAndCustomNotesModels/CustomNotificationViewModel";
import {CustomNotificationChipComponent} from "./custom-notification-chip/custom-notification-chip.component";
import {
  CustomNoteStateRequest,
  CustomNotificationService
} from "../../services/facade-services/custom-notification-service/custom-notification.service";
import {MatTooltip} from "@angular/material/tooltip";
import {BreakpointObserver} from "@angular/cdk/layout";
import {
  MyNotificationsAccordionBodyComponent
} from "./my-notifications-accordion-body/my-notifications-accordion-body.component";
import {
  PushNotificationFormComponent
} from "../user-profile-notification-forms/push-notification-form/push-notification-form.component";
import {PushSubViewModel} from "../../models/NotificationPrefAndCustomNotesModels/PushSubViewModel";
import {MatSnackBar} from "@angular/material/snack-bar";
import {PushSubscriptionChipComponent} from "./push-subscription-chip/push-subscription-chip.component";

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
    CustomNotificationChipComponent,
    AsyncPipe,
    MatTooltip,
    MyNotificationsAccordionBodyComponent,
    PushNotificationFormComponent,
    PushSubscriptionChipComponent,
  ],
  styleUrl: './profile-notifications-tab.component.css'
})
export class ProfileNotificationsTabComponent implements OnDestroy {
  private destroy$ = new Subject<void>();

  public hasDiscordAcct!: boolean;
  public doNotShowCustomNotesForm: boolean = true;

  protected myPushSubscriptions: PushSubViewModel[] = [];
  protected noPushSubs: boolean = false;

  public doNotShowPushNotesForm: boolean = true;

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

  constructor(protected userService: UserService,
              private noteSettingsApiService: NotificationSettingsApiService,
              protected customNoteService: CustomNotificationService,
              private snackBar: MatSnackBar) {

    this.getUserDiscNotePrefs();
    this.getMyPushSubs();

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

    this.customNoteService.userCustomNotes$.pipe(takeUntil(this.destroy$))
      .subscribe(notes => {
        this.userCustomNotes = notes;
        this.noCustomNotes = this.userCustomNotes.length === 0;
      })

    this.customNoteService.getMyCustomNotifications();
  }

  showSubscribeOnThisDevice() {
    this.doNotShowPushNotesForm = !this.doNotShowPushNotesForm;
  }

  createPushSubSuccessTrigger(val: boolean) {
    if(val) {
      this.showSubscribeOnThisDevice();

      this.snackBar.open('Push subscription created successfully', 'OK', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['mobile-snackbar']
      })

      this.getMyPushSubs();
    }
    else {
      this.snackBar.open('There was an error creating your push subscription.', 'OK', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['mobile-snackbar']
      })
    }
  }

  getMyPushSubs() {
    this.noteSettingsApiService.getMyPushSubs().pipe(takeUntil(this.destroy$))
      .subscribe(page => {
        console.log(page.content);
        this.myPushSubscriptions = page.content;
        this.noPushSubs = page.content.length === 0;
      });
  }

  showCustomNoteForm(){
    this.doNotShowCustomNotesForm = !this.doNotShowCustomNotesForm;
  }

  createOrUpdateSuccess(val: boolean) {
    this.doNotShowCustomNotesForm = val;
    if(val) {
      this.customNoteService.getMyCustomNotifications();
    }
  }

  sendCustomNoteStateChangeRequest(state: CustomNoteStateRequest) {
    this.customNoteService.sendEnabledStateChangeRequest(state);
  }

  getUserDiscNotePrefs() {
    this.discSysNotesControl.setValue(this.userService.sysNotesEnabled ?? false);
    this.discGroupNotesControl.setValue(this.userService.groupNotesEnabled ?? false);
    this.discSocialNotesControl.setValue(this.userService.socialNotesEnabled ?? false);
  }

  updateDiscordSysNotesPref() {
    const label: string = 'sysNotes';
    const val = this.discSysNotesControl.value;

    const updateRequest = new UpdateNotificationPreferenceRequest(label, val);

    this.noteSettingsApiService.updateDiscordNotificationPreference(updateRequest).subscribe(
      response => {
        this.userService.refreshUser();
        if(response === this.discSysNotesControl.value) {
          this.discSysNotesSaved = true;
          setTimeout(() => this.discSysNotesSaved = false, 2000);
        }
        else {
          this.discSysNotesSaveError = true;
          setTimeout(() => this.discSysNotesSaved = false, 2000);
          this.getUserDiscNotePrefs();
        }
      }
    );
  }

  updateDiscordGroupNotesPref() {
    const label: string = 'groupNotes';
    const val = this.discGroupNotesControl.value;

    const updateRequest = new UpdateNotificationPreferenceRequest(label, val);

    this.noteSettingsApiService.updateDiscordNotificationPreference(updateRequest).subscribe(
      response => {
        this.userService.refreshUser();
        if(response === this.discGroupNotesControl.value) {
          this.discGroupNotesSaved = true;
          setTimeout(() => this.discGroupNotesSaved = false, 2000);
        }
        else {
          this.discGroupNotesSaveError = true;
          setTimeout(() => this.discGroupNotesSaved = false, 2000);
          this.getUserDiscNotePrefs();
        }
      }
    )
  }

  updateDiscordSocialNotesPref() {
    const label: string = 'socialNotes';
    const val = this.discSocialNotesControl.value;

    const updateRequest = new UpdateNotificationPreferenceRequest(label, val);

    this.noteSettingsApiService.updateDiscordNotificationPreference(updateRequest).subscribe(
      response => {
        this.userService.refreshUser();
        if(response === this.discSocialNotesControl.value) {
          this.discSocialNotesSaved = true;
          setTimeout(() => this.discSocialNotesSaved = false, 2000);
        }
        else {
          this.discSocialNotesSaveError = true;
          setTimeout(() => this.discSocialNotesSaved = false, 2000);
          this.getUserDiscNotePrefs();
        }
      }
    )
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
