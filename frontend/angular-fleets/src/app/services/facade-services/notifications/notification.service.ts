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

  private notePageIdx: number = 0;
  private notePageSize: number = 10;
  private noteTotalElements: number = 0;
  private noteTotalPages: number = 0;
  private noNotifications: boolean = true;

  public closingIds: Set<number> = new Set<number>();

  constructor(private ws: WsGatewayService,
              private noteApi: NotificationApiService) {
  }

  setOpenState(open: boolean): void {
    this.openStateSubject.next(open);
  }

  loadNotifications() {
    this.noteApi.getMyNotifications(this.notePageIdx, this.notePageSize)
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

  removeNotification(noteId: number) {
    this.noteApi.deleteNotification(noteId).pipe(takeUntilDestroyed(this.notesDestroyRef))
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
              this.ws.setNotesUnread(next.length);
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
