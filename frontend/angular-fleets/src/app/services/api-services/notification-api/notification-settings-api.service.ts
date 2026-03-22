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
import {
  CustomNotesPageAndEnabledCountResponse,
  CustomNoteStateRequest
} from "../../facade-services/custom-notification-service/custom-notification.service";
import {PushSubViewModel} from "../../../models/NotificationPrefAndCustomNotesModels/PushSubViewModel";
import {Page} from "../group-listings-fetch-api/group-listing-fetch.service";
import {
  PushSubPrefChangeRequest
} from "../../../components/user-profile-notification-settings-tab/push-subscription-chip/push-subscription-chip.component";

export interface NewPushSubscription {
  userLabel: string,
  deviceUrl: string,
  publicKey: string,
  browserSecret: string
}

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

  editCustomNotificationData(noteId: number, edited: CreateOrUpdateCustomNoteRequest) {
    return this.httpClient.put<CustomNotificationViewModel>(
      `${this.notePrefsBaseUrl}/edit_custom_notification/${noteId}`, edited)
  }

  customNotificationStateChangeRequest(request: CustomNoteStateRequest) {
    return this.httpClient.patch<CustomNotificationViewModel>(
      `${this.notePrefsBaseUrl}/custom_notification_state_change/${request.idCustomNote}`, null,
      {params: { enabledState: request.enabledState }});
  }

  deleteCustomNotification(noteId: number) {
    return this.httpClient.delete(`${this.notePrefsBaseUrl}/delete_custom_notification/${noteId}`);
  }

  getMyPushSubs() {
    const pageRequest = {
      pageIdx: 0,
      pageSize: 10
    }

    return this.httpClient.post<Page<PushSubViewModel>>(`${this.notePrefsBaseUrl}/get_my_push_subs`, pageRequest);
  }

  savePushSubscription(payload: NewPushSubscription): Observable<PushSubViewModel> {
    return this.httpClient.post<PushSubViewModel>(`${this.notePrefsBaseUrl}/create_push_sub`, payload);
  }

  updatePushSubNotificationPreference(pref: PushSubPrefChangeRequest): Observable<PushSubViewModel> {
    return this.httpClient.put<PushSubViewModel>(`${this.notePrefsBaseUrl}/update_push_sub`, pref);
  }

  updateDiscordNotificationPreference(pref: UpdateNotificationPreferenceRequest) {
    return this.httpClient.put(`${this.notePrefsBaseUrl}/update_discord_notification_pref`, pref);
  }

}
