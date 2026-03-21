import { Injectable } from '@angular/core';
import {environment} from "../../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {
  CreateOrUpdateCustomNoteRequest
} from "../../../models/NotificationPrefAndCustomNotesModels/CreateOrUpdateCustomNoteRequest";
import {
  CustomNotificationViewModel
} from "../../../models/NotificationPrefAndCustomNotesModels/CustomNotificationViewModel";
import {
  UpdateNotificationPreferenceRequest
} from "../../../models/NotificationPrefAndCustomNotesModels/update-notification-preference-request";
import {Observable} from "rxjs";
import {Page} from "../group-listings-fetch-api/group-listing-fetch.service";
import {
  CustomNotesPageAndEnabledCountResponse,
  CustomNoteStateRequest
} from "../../facade-services/custom-notification-service/custom-notification.service";

@Injectable({
  providedIn: 'root'
})
export class NotificationSettingsApiService {

  private notePrefsBaseUrl = `${environment.apiBaseUrl}/user_notification_preferences`;

  constructor(private httpClient: HttpClient) { }

  getMyCustomNotifications(idx: number, page: number): Observable<CustomNotesPageAndEnabledCountResponse> {
    const req = {
      pageIdx: idx,
      pageSize: page,
    }

    return this.httpClient.post<CustomNotesPageAndEnabledCountResponse>(
      `${this.notePrefsBaseUrl}/get_my_custom_notifications`, req).pipe(

    )
  }

  createCustomNotification(request: CreateOrUpdateCustomNoteRequest) {
    return this.httpClient.post<CustomNotificationViewModel>(
      `${this.notePrefsBaseUrl}/create_custom_notification`, request);
  }

  deleteCustomNotification(noteId: number) {
    return this.httpClient.delete(`${this.notePrefsBaseUrl}/delete_custom_notification/${noteId}`);
  }

  updateExternalNotificationPreference(pref: UpdateNotificationPreferenceRequest) {
    return this.httpClient.put(`${this.notePrefsBaseUrl}/update_discord_notification_pref`, pref);
  }

  customNotificationStateChangeRequest(request: CustomNoteStateRequest) {

    return this.httpClient.patch<CustomNotificationViewModel>(
      `${this.notePrefsBaseUrl}/custom_notification_state_change/${request.idCustomNote}`, null,
      {params: { enabledState: request.enabledState }});
  }
}
