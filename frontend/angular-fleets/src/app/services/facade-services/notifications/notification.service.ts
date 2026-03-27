import {DestroyRef, inject, Injectable} from '@angular/core';
import {WsGatewayService} from "../../websocket-messaging/ws-gateway.service";
import {NotificationApiService} from "../../api-services/notification-api/notification-api.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {NotificationViewModel} from "../../../models/NotificationViewModel";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})

export class NotificationService {
  private notesDestroyRef = inject(DestroyRef)

  private openStateSubject = new BehaviorSubject<boolean>(false);
  public openState$ = this.openStateSubject.asObservable();

  private myNotificationsSubject = new BehaviorSubject<NotificationViewModel[]>([]);
  public myNotifications$ = this.myNotificationsSubject.asObservable();

  private notePageIdx: number = 0;
  private notePageSize: number = 10;
  private noteTotalElements: number = 0;
  private noteTotalPages: number = 0;
  private noNotifications: boolean = true;

  public closingIds: Set<number> = new Set<number>();

  constructor(private ws: WsGatewayService,
              private noteApi: NotificationApiService) {
  }

  loadMyNotifications(page: NotificationViewModel[]) {
    this.myNotificationsSubject.next(page);
  }

  setOpenState(open: boolean): void {
    this.openStateSubject.next(open);
  }

  loadDropdownNotifications() {
    this.noteApi.getMyDropdownNotifications(this.notePageIdx, this.notePageSize)
      .pipe(takeUntilDestroyed(this.notesDestroyRef))
      .subscribe(page => {
        this.noteTotalElements = page.page.totalElements;
        this.notePageIdx = page.page.number;
        this.notePageSize = page.page.size;
        this.noteTotalPages = page.page.totalPages;
        this.noNotifications = page.content.length === 0;

        this.ws.setNotesArray(page.content)
      })
  }

  removeNotificationDropdownPriority(noteId: number) {
    this.noteApi.removeNoteDropdownPriority(noteId).pipe(takeUntilDestroyed(this.notesDestroyRef))
      .subscribe( {
        next: () => {
            this.closingIds.add(noteId);
            const notes = this.ws.getNotesArray();
            const idx = notes.findIndex(n => n.notificationId === noteId);

            let next: NotificationViewModel[];
            if(idx > -1) {
              next = [...notes];
              next.splice(idx, 1);
            } else {
              next = [...notes]
            }
            setTimeout(() => {
              this.closingIds.delete(noteId)
              this.ws.setNotesArray(next);
            }, 300);
        },
        error: (err: any) => {
          alert(err.error.message);
        }
      })
  }

  setRead(scanned: number[]) {
    this.ws.publish('/app/system.notify/receive_read', {
      readIds: scanned
    })
  }
}
