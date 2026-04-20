import {DestroyRef, inject, Injectable} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Page} from "../group-listings-fetch-api/group-listing-fetch.service";
import {
  GroupManagementMemberViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {Observable} from "rxjs";
import {
  GroupManagementInviteViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-management-invite-view-model";
import {SendGroupInviteOffer} from "../../../models/group-management-models/request-models/send-group-invite-offer";

@Injectable({
  providedIn: 'root'
})
export class MemberManagementApiService {
  private destroyRef = inject(DestroyRef);

  private baseUrl = `${environment.apiBaseUrl}/group-member-management`;

  constructor(private httpClient: HttpClient) {}

  verifyGroupManagementAuthorization(listingId: number): Observable<Map<string, boolean>> {
    return this.httpClient.get<Map<string, boolean>>(
      `${this.baseUrl}/verify_group_management_authz/${listingId}`);
  }

  getActiveRosterGroupMembers(listingId: number): Observable<Page<GroupManagementMemberViewModel>> {
    return this.httpClient.get<Page<GroupManagementMemberViewModel>>(`${this.baseUrl}/get_active_roster/${listingId}`);
  }

  getWaitListMembers(listingId: number): Observable<Page<GroupManagementMemberViewModel>> {
    return this.httpClient.get<Page<GroupManagementMemberViewModel>>(
      `${this.baseUrl}/get_waitlist_members/${listingId}`);
  }

  getGroupInvites(listingId: number): Observable<Page<GroupManagementInviteViewModel>> {
    return this.httpClient.get<Page<GroupManagementInviteViewModel>>(
      `${this.baseUrl}/get_group_invites/${listingId}`);
  }

  acceptGroupInviteRequest(invRequest: GroupManagementInviteViewModel): Observable<GroupManagementMemberViewModel> {
    return this.httpClient.post<GroupManagementMemberViewModel>(`${this.baseUrl}/accept_group_invite_request`, invRequest);
  }

  sendGroupInviteOffer(invOffer: SendGroupInviteOffer): Observable<GroupManagementInviteViewModel> {
    return this.httpClient.post<GroupManagementInviteViewModel>(`${this.baseUrl}/send_invite`, invOffer);
  }
}
