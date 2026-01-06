import {DestroyRef, inject, Injectable} from '@angular/core';
import {WsGatewayService} from "../../websocket-messaging/ws-gateway.service";
import {NotificationApiService} from "../../api-services/notification-api/notification-api.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notesDestroyRef = inject(DestroyRef)

  private notePageIdx: number = 0;
  private notePageSize: number = 10;
  private noteTotalElements: number = 0;
  private noteTotalPages: number = 0;
  private noNotifications: boolean = true;



  constructor(private ws: WsGatewayService,
              private noteApi: NotificationApiService) {
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
}
