import {Component, EventEmitter, OnDestroy, Output} from '@angular/core';
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
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-push-notification-form',
  standalone: true,
  templateUrl: './push-notification-form.component.html',
  imports: [
    GenericSmallInputFieldComponent,
    NgIf
  ],
  styleUrl: './push-notification-form.component.css'
})
export class PushNotificationFormComponent implements OnDestroy {
  private destroy$ = new Subject<void>();
  @Output() cancelForm= new EventEmitter<boolean>();
  @Output() formSuccess = new EventEmitter<boolean>();

  protected showFormErrorMessage: string | null = null;

  private pushSubFormSubmitSubject = new BehaviorSubject<boolean>(false);
  public pushSubFormSubmit$ = this.pushSubFormSubmitSubject.asObservable();

  pushSubInputCtrl: FormControl<string> = new FormControl<string>('', {
    validators: [Validators.required,
                 Validators.minLength(3),
                 Validators.maxLength(32)],
                 nonNullable: true
  });

  constructor(private noteSettingsApiService: NotificationSettingsApiService){}

  async enablePushNotifications() {
    if(this.pushSubInputCtrl.invalid){
      this.pushSubInputCtrl.markAsDirty()
      this.pushSubInputCtrl.markAsTouched();
      return;
    }
    const permission = await Notification.requestPermission();

    if(permission !== 'granted') {
      this.showFormErrorMessage = permission;
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

    this.noteSettingsApiService.savePushSubscription(newPushSubRequest).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (!environment.production) {
            console.log("New Push Subscription: ", JSON.stringify(response))
          }
          this.pushSubInputCtrl.reset();
          this.showFormErrorMessage = null;
          this.formSuccess.emit(true);
        },
        error: (e) => {
          this.formSuccess.emit(false);
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

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
