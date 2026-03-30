import {Component, OnDestroy} from '@angular/core';
import {Subject, takeUntil} from "rxjs";
import {NotificationApiService} from "../../../services/api-services/notification-api/notification-api.service";
import {NotificationService} from "../../../services/facade-services/notifications/notification.service";
import {MatDialog} from "@angular/material/dialog";
import {MatSnackBar} from "@angular/material/snack-bar";
import {AsyncPipe, NgIf} from "@angular/common";
import {NotificationChipGenericComponent} from "../notification-chip-generic/notification-chip-generic.component";
import {ConfirmGenericComponent} from "../../pop-ups/confirm-generic/confirm-generic.component";

@Component({
  selector: 'app-my-notifications-accordion-body',
  standalone: true,
  templateUrl: './my-notifications-accordion-body.component.html',
  imports: [
    NgIf,
    AsyncPipe,
    NotificationChipGenericComponent
  ],
  styleUrl: './my-notifications-accordion-body.component.css'
})
export class MyNotificationsAccordionBodyComponent implements OnDestroy {
  private destroy$: Subject<void> = new Subject<void>();

  public noNotifications: boolean = false;

  constructor(private notesApiService: NotificationApiService,
              protected notesService: NotificationService,
              private dialog: MatDialog,
              private snackBar: MatSnackBar) {

    this.getAllMyNotifications();
  }

  getAllMyNotifications() {
    const idx: number = 0;
    const size: number = 50;

    this.notesApiService.getAllMyNotifications(idx, size).pipe(takeUntil(this.destroy$))
      .subscribe(page => {
        this.notesService.loadMyNotifications(page.content);
        this.noNotifications = page.content.length === 0;
      })
  }

  deleteSingleNoteEvent(noteId: number) {
    this.notesApiService.deleteNotification(noteId).pipe(takeUntil(this.destroy$))
      .subscribe(() => {
          this.getAllMyNotifications();
      })
  }

  confirmDeleteAllNotifications() {
    const dialogRef = this.dialog.open(ConfirmGenericComponent, {
      data: {
        message: ('Confirm that you would like to permanently delete all of your existing notifications.'),
        title: null
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if(result) {
        this.notesApiService.deleteAllMyNotifications().pipe(takeUntil(this.destroy$))
          .subscribe(() => {
            this.getAllMyNotifications();
          })
      }
    })
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
