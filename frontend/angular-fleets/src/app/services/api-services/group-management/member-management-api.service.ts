import {DestroyRef, inject, Injectable} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {HttpClient, HttpResponse, HttpStatusCode} from "@angular/common/http";
import {
  GroupManagementMemberViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {Observable} from "rxjs";
import {
  GroupManagementInviteViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-management-invite-view-model";
import {SendGroupInviteOffer} from "../../../models/group-management-models/request-models/send-group-invite-offer";
import {Page} from "../../../models/page-interface";
import {
  UserMonikerSummaryViewModel
} from "../../../models/group-management-models/nested-models/user-moniker-summary-view-model";

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
    return this.httpClient.get<Page<GroupManagementMemberViewModel>>(
      `${this.baseUrl}/get_active_roster/${listingId}`);
  }

  getWaitListMembers(listingId: number): Observable<Page<GroupManagementMemberViewModel>> {
    return this.httpClient.get<Page<GroupManagementMemberViewModel>>(
      `${this.baseUrl}/get_waitlist_members/${listingId}`);
  }

  getGroupInvites(listingId: number): Observable<Page<GroupManagementInviteViewModel>> {
    return this.httpClient.get<Page<GroupManagementInviteViewModel>>(
      `${this.baseUrl}/get_group_invites/${listingId}`);
  }

  newMemberFromJoinRequest(invRequest: GroupManagementInviteViewModel): Observable<GroupManagementMemberViewModel> {
    return this.httpClient.post<GroupManagementMemberViewModel>(
      `${this.baseUrl}/new_member_from_join_request`, invRequest);
  }

  declineGroupInviteRequest(invId: number): Observable<GroupManagementInviteViewModel> {
    return this.httpClient.put<GroupManagementInviteViewModel>(
      `${this.baseUrl}/decline_group_invite_request/${invId}`, {})
  }

  blockInviteRequests(invId: number): Observable<UserMonikerSummaryViewModel> {
    return this.httpClient.put<UserMonikerSummaryViewModel>(
      `${this.baseUrl}/block_join_requests/${invId}`, {}
    )
  }

  mirrorActiveRequestToWaitlistInvite(invId: number): Observable<number> {
    return this.httpClient.put<number>(`${this.baseUrl}/convert_active_roster_request_to_waitlist_invite/${invId}`, {});
  }

  sendGroupInviteOffer(invOffer: SendGroupInviteOffer): Observable<GroupManagementInviteViewModel> {
    return this.httpClient.post<GroupManagementInviteViewModel>(
      `${this.baseUrl}/send_invite`, invOffer);
  }

  rescindGroupInviteOffer(invId: number): Observable<GroupManagementInviteViewModel> {
    return this.httpClient.put<GroupManagementInviteViewModel>(
      `${this.baseUrl}/rescind_invite_offer/${invId}`, {});
  }

  managerDismissInvite(invId: number): Observable<void> {
    return this.httpClient.put<void>(`${this.baseUrl}/manager_dismiss_invite/${invId}`, {});
  }
}
