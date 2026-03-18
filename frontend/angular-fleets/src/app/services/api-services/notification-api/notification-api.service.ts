import { Injectable } from '@angular/core';
import {environment} from "../../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Page} from "../group-listings-fetch-api/group-listing-fetch.service";
import {map, Observable} from "rxjs";
import {NotificationViewModel} from "../../../models/NotificationViewModel";
import {UpdateNotificationPreferenceRequest} from "../../../models/private-user/update-notification-preference-request";

@Injectable({
  providedIn: 'root'
})
export class NotificationApiService {

  private getUrl = `${environment.apiBaseUrl}/notify/my_notifications`;
  private deleteUrl = `${environment.apiBaseUrl}/notify/delete`;
  private deleteAllUrl = `${environment.apiBaseUrl}/notify/delete_all`;
  private notePrefsBaseUrl = `${environment.apiBaseUrl}/user_notification_preferences`;

  constructor(private httpClient: HttpClient) { }

  getMyNotifications(pageIdx: number, pageSize: number): Observable<Page<NotificationViewModel>> {
    const pageRequest = {
      pageIdx: pageIdx,
      pageSize: pageSize
    };

    return this.httpClient.post<Page<NotificationViewModel>>(this.getUrl, pageRequest)
  }

  deleteNotification(noteId: number): Observable<any> {
    return this.httpClient.delete(`${this.deleteUrl}/${noteId}`);
  }

  deleteAllMyNotifications() {
    return this.httpClient.delete(`${this.deleteAllUrl}/notify/delete_all`);
  }

  public updateExternalNotificationPreference(pref: UpdateNotificationPreferenceRequest) {
    return this.httpClient.put<{label: string}>(`${this.notePrefsBaseUrl}/update_discord_notification_pref`, pref).pipe(
      map(response => response.label)
    );
  }
}
