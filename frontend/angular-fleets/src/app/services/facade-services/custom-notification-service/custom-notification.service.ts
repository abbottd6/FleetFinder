import {DestroyRef, inject, Injectable} from '@angular/core';
import {BehaviorSubject, firstValueFrom, map, Observable} from "rxjs";
import {Page} from "../../api-services/group-listings-fetch-api/group-listing-fetch.service";
import {
  CustomNotificationViewModel
} from "../../../models/NotificationPrefAndCustomNotesModels/CustomNotificationViewModel";
import {environment} from "../../../../environments/environment";
import {NotificationSettingsApiService} from "../../api-services/notification-api/notification-settings-api.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {HttpErrorResponse} from "@angular/common/http";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmGenericComponent} from "../../../components/pop-ups/confirm-generic/confirm-generic.component";

export interface CustomNoteStateRequest {
  idCustomNote: number,
  enabledState: boolean
}

export interface CustomNotesPageAndEnabledCountResponse {
  userCustomNotes: Page<CustomNotificationViewModel>,
  enabledCount: number,
}

@Injectable({
  providedIn: 'root'
})
export class CustomNotificationService {

  private destroyRef = inject(DestroyRef);

  private userEnabledCountSubject = new BehaviorSubject<number>(0);
  public userCustomNotesEnabledCount$ = this.userEnabledCountSubject.asObservable();

  private userCustomNotesSubject = new BehaviorSubject<CustomNotificationViewModel[]>([]);
  public userCustomNotes$ = this.userCustomNotesSubject.asObservable();

  private customNoteForEditSubject = new BehaviorSubject<CustomNotificationViewModel | null>(null);
  public customNoteForEdit$ = this.customNoteForEditSubject.asObservable();

  constructor(private noteSettingsApiService: NotificationSettingsApiService,
              private snackBar: MatSnackBar, private dialog: MatDialog) {}

  getMyCustomNotifications() {
    const IDX: number = 0;
    const PAGE: number = 20;

    this.noteSettingsApiService.getMyCustomNotifications(IDX, PAGE).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
          next: (body: CustomNotesPageAndEnabledCountResponse) => {
            if(!environment.production) {
              console.log('custom notes logged: ', body.userCustomNotes);
            }
            this.userCustomNotesSubject.next((body.userCustomNotes.content).reverse());
            this.userEnabledCountSubject.next(body.enabledCount);
          },
          error: (err) => {
            console.error('Error fetching user\'s custom notifications', err);
          }
        }
      )
  }

  sendEnabledStateChangeRequest(request: CustomNoteStateRequest) {
    this.noteSettingsApiService.customNotificationStateChangeRequest(request).pipe(
      takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: CustomNotificationViewModel) => {
          this.getMyCustomNotifications();
        },
        error: (err) => {
          if(err instanceof HttpErrorResponse && err.status === 403) {
            this.snackBar.open('Error: You can only have 5 custom notifications enabled at a time.', 'OK', {
              duration: 4000,
              verticalPosition: 'top',
              horizontalPosition: 'center',
              panelClass: ['mobile-snackbar']
            })
          }
          setTimeout(() => this.getMyCustomNotifications(), 500);
        }
      }
    );
  }

  setNoteForEdit(note: CustomNotificationViewModel) {
    this.customNoteForEditSubject.next(note);
  }

  get noteForEdit() {
    return this.customNoteForEditSubject.value;
  }

  resetNoteForEdit() {
    this.customNoteForEditSubject.next(null);
  }

  openConfirmDeleteCustomNote(noteId: number, noteLabel: string) {
    const dialogRef = this.dialog.open(ConfirmGenericComponent, {
      data: {
        message: (`Confirm that you would like to delete this custom notification: \n  \n ${noteLabel}`),
        title: null
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if(result) {
        this.deleteCustomNotificationRequest(noteId);
      }
    })
  }

  deleteCustomNotificationRequest(noteId: number) {
    this.noteSettingsApiService.deleteCustomNotification(noteId).pipe(
      takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: () => {
            this.snackBar.open('CustomNotification deleted successfully.', 'OK', {
              duration: 3000,
              verticalPosition: 'top',
              horizontalPosition: 'center',
              panelClass: ['mobile-snackbar']
            })
            this.getMyCustomNotifications();
          },
          error: (err) => {
            this.snackBar.open('Request Failed.', 'OK', {
              duration: 4000,
              verticalPosition: 'top',
              horizontalPosition: 'center',
              panelClass: ['mobile-snackbar']
            })
          }
        })
  }

}
