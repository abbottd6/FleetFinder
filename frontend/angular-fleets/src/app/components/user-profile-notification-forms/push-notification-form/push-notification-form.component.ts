import {Component, EventEmitter, Input, OnChanges, OnDestroy, Output, SimpleChanges} from '@angular/core';
import {BehaviorSubject, Subject, takeUntil} from "rxjs";
import {FormControl, Validators} from "@angular/forms";
import {
  GenericSmallInputFieldComponent
} from "../../input-fields/generic-small-input-field/generic-small-input-field.component";
import {environment} from "../../../../environments/environment";
import {
  NewPushSubscription,
  NotificationSettingsApiService
} from "../../../services/api-services/notification-api/notification-settings-api.service";
import {AsyncPipe, NgIf} from "@angular/common";
import {HttpErrorResponse} from "@angular/common/http";
import {MatDialog} from "@angular/material/dialog";
import {
  CreatePushSubResponsePopupComponent
} from "../../pop-ups/create-push-sub-response-popup/create-push-sub-response-popup.component";

@Component({
  selector: 'app-push-notification-form',
  standalone: true,
  templateUrl: './push-notification-form.component.html',
  imports: [
    GenericSmallInputFieldComponent,
    NgIf,
    AsyncPipe
  ],
  styleUrl: './push-notification-form.component.css'
})
export class PushNotificationFormComponent implements OnDestroy, OnChanges {
  private destroy$ = new Subject<void>();

  @Output() cancelForm= new EventEmitter<boolean>();
  @Output() formSuccess = new EventEmitter<boolean>();
  @Output() formError = new EventEmitter<HttpErrorResponse>();

  @Input() triggerBrowserCheck: boolean = false;

  protected showFormErrorMessage: string | null = null;
  protected browserIncompatibilityMessage: string | null = null;

  private pushSubFormSubmitSubject = new BehaviorSubject<boolean>(false);
  public pushSubFormSubmit$ = this.pushSubFormSubmitSubject.asObservable();

  pushSubInputCtrl: FormControl<string> = new FormControl<string>('', {
    validators: [Validators.required,
                 Validators.minLength(3),
                 Validators.maxLength(32)],
                 nonNullable: true
  });

  constructor(private noteSettingsApiService: NotificationSettingsApiService, private dialog: MatDialog) {}

  ngOnChanges(changes: SimpleChanges) {
    if(changes['triggerBrowserCheck']?.currentValue === true) {
      this.browserIncompatibilityMessage = this.determineBrowserCompatibility();
    }
  }

  async enablePushNotifications() {
    this.pushSubFormSubmitSubject.next(true);

    if(this.pushSubInputCtrl.invalid){
      this.pushSubInputCtrl.markAsDirty()
      this.pushSubInputCtrl.markAsTouched();
      return;
    }

    const permission = await Notification.requestPermission();

    if(permission !== 'granted') {
      this.showFormErrorMessage = 'Your browser is blocking the request: ' + permission;
      return;
    }

    const registration = await navigator.serviceWorker.ready;
    const subscription = await registration.pushManager.subscribe({
      userVisibleOnly: true,
      applicationServerKey: this.urlBase64ToUint8Array(environment.vapidPublicKey)
    });

    const sub = subscription.toJSON();

    if(sub.endpoint == null || sub.keys == null) return;

    const newPushSubRequest: NewPushSubscription = {
      userLabel: this.pushSubInputCtrl.value,
      deviceUrl: sub.endpoint,
      publicKey: sub.keys['p256dh'],
      browserSecret: sub.keys['auth']
    }

    if(newPushSubRequest.deviceUrl.toString().includes('permanently-removed.invalid')) {
      this.showFormErrorMessage = "Push subscription key is invalid. Please try a different browser or device.";
      return;

    }

    this.noteSettingsApiService.savePushSubscription(newPushSubRequest).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (!environment.production) {
            console.log("New Push Subscription: ", JSON.stringify(response))
          }
          this.showFormErrorMessage = null;

          const dialogRef = this.dialog.open(CreatePushSubResponsePopupComponent, {
            data: {
              response: response,
              error: null
            }
          });

          dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(() => {
            this.pushSubInputCtrl.reset();
            this.formSuccess.emit(true);
            this.pushSubFormSubmitSubject.next(false);
          })

        },
        error: (err) => {
          const dialogRef = this.dialog.open(CreatePushSubResponsePopupComponent, {
            data: {
              response: null,
              error: err
            }
          });

          dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(() => {
            this.pushSubFormSubmitSubject.next(false);
          })
        }
      });
  }

  emitCancelClick() {
    this.showFormErrorMessage = null;
    this.pushSubInputCtrl.reset();
    this.cancelForm.emit(true);
  }

  private urlBase64ToUint8Array(base: string): Uint8Array {
    const padding = '='.repeat((4 - base.length % 4) % 4);
    const base64 = (base + padding)
      .replace(/-/g, '+')
      .replace(/_/g, '/');

    const rawData = atob(base64);

    return Uint8Array.from([...rawData].map(char => char.charCodeAt(0)));
  }

  determineBrowserCompatibility() {
      const ua = navigator.userAgent;

      const deviceMsg = " devices do not support push notifications."
      const browserMsg = " do[es] not support push notifications."

      if (/iPhone|iPad|iPod/i.test(ua)) return 'iOS' + deviceMsg;
      if (/FBAN|FBAV/i.test(ua)) return 'In-app browsers' + browserMsg;
      if (/Instagram/i.test(ua)) return 'In-app browsers' + browserMsg;
      if (/wv/.test(ua) && /Android/i.test(ua)) return 'Android WebView' + browserMsg;
      if (/Edg\//.test(ua) && /Mobile/i.test(ua)) return 'Edge Mobile' + browserMsg;
      return null;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
