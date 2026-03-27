import { Injectable } from '@angular/core';
import {environment} from "../../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Page} from "../group-listings-fetch-api/group-listing-fetch.service";
import {map, Observable} from "rxjs";
import {NotificationViewModel} from "../../../models/NotificationViewModel";
import {UpdateNotificationPreferenceRequest} from "../../../models/NotificationPrefAndCustomNotesModels/update-notification-preference-request";
import {
  CreateOrUpdateCustomNoteRequest
} from "../../../models/NotificationPrefAndCustomNotesModels/CreateOrUpdateCustomNoteRequest";
import {
  CustomNotificationViewModel
} from "../../../models/NotificationPrefAndCustomNotesModels/CustomNotificationViewModel";

@Injectable({
  providedIn: 'root'
})
export class NotificationApiService {

  private getAllUrl = `${environment.apiBaseUrl}/notify/all_my_notifications`;
  private getDropdownPriorityUrl = `${environment.apiBaseUrl}/notify/my_dropdown_notifications`;
  private removePriorityUrl = `${environment.apiBaseUrl}/notify/dropdown_remove`;
  private deleteUrl = `${environment.apiBaseUrl}/notify/delete`;
  private deleteAllUrl = `${environment.apiBaseUrl}/notify/delete_all`;

  constructor(private httpClient: HttpClient) { }

  getMyDropdownNotifications(pageIdx: number, pageSize: number): Observable<Page<NotificationViewModel>> {
    const pageRequest = {
      pageIdx: pageIdx,
      pageSize: pageSize
    };

    return this.httpClient.post<Page<NotificationViewModel>>(this.getDropdownPriorityUrl, pageRequest)
  }

  getAllMyNotifications(pageIdx: number, pageSize: number): Observable<Page<NotificationViewModel>> {
    const pageRequest = {
      pageIdx: pageIdx,
      pageSize: pageSize
    }

    return this.httpClient.post<Page<NotificationViewModel>>(this.getAllUrl, pageRequest)
  }

  deleteNotification(noteId: number): Observable<any> {
    return this.httpClient.delete(`${this.deleteUrl}/${noteId}`);
  }

  removeNoteDropdownPriority(noteId: number) {
    return this.httpClient.delete(`${this.removePriorityUrl}/${noteId}`)
  }

  deleteAllMyNotifications() {
    return this.httpClient.delete(`${this.deleteAllUrl}`);
  }
}
