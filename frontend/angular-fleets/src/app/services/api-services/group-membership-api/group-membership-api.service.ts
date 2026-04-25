  import {DestroyRef, inject, Injectable} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs";
import {
  GroupMembershipViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-membership-view-model";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
  import {tap} from "rxjs/operators";
  import {
    SendGroupInviteRequest
  } from "../../../models/group-management-models/request-models/send-group-invite-request";
  import {
    GroupInviteViewModel
  } from "../../../models/group-management-models/view-models/group-membership/group-invite-view-model";
  import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
  import {Page} from "../../../models/page-interface";

export const rosterClasses: string[] = ['Active', 'Waitlist'];

@Injectable({
  providedIn: 'root'
})
export class GroupMembershipApiService {
  private destroyRef = inject(DestroyRef);

  private baseUrl = `${environment.apiBaseUrl}/group-membership`;

  constructor(private httpClient: HttpClient) {}

  getMyGroupMemberships(): Observable<Page<GroupMembershipViewModel>> {
    return this.httpClient.get<Page<GroupMembershipViewModel>>(`${this.baseUrl}/my_groups`).pipe(
      takeUntilDestroyed(this.destroyRef),
      tap(page => page)
      );
  }

  getMyGroupInvites(pageIdx: number, pageSize: number): Observable<Page<GroupInviteViewModel>> {
    const pageRequest = {
      pageIdx: pageIdx,
      pageSize: pageSize
    }

    return this.httpClient.post<Page<GroupInviteViewModel>>(`${this.baseUrl}/my_invites`, pageRequest);
  }

  getMyInviteAuthorizedMemberships(): Observable<Page<GroupListingViewModel>> {
    return this.httpClient.get<Page<GroupListingViewModel>>(`${this.baseUrl}/my_invite_authorized_groups`);
  }

  sendGroupInviteRequest(invRequest: SendGroupInviteRequest): Observable<GroupInviteViewModel> {
    return this.httpClient.post<GroupInviteViewModel>(`${this.baseUrl}/request_invite`, invRequest);
  }

  acceptGroupInviteOffer(invOffer: GroupInviteViewModel): Observable<GroupMembershipViewModel> {
    return this.httpClient.post<GroupMembershipViewModel>(`${this.baseUrl}/accept_group_invite_offer`, invOffer);
  }

  declineGroupInviteOffer(invId: number): Observable<GroupInviteViewModel> {
    return this.httpClient.put<GroupInviteViewModel>(`${this.baseUrl}/decline_group_invite_offer/${invId}`, {});
  }

  rescindJoinRequest(invId: number): Observable<GroupInviteViewModel> {
    return this.httpClient.put<GroupInviteViewModel>(`${this.baseUrl}/rescind_group_invite_join_request/${invId}`, {});
  }

  dismissActionedInvite(invId: number): Observable<void> {
    return this.httpClient.put<void>(`${this.baseUrl}/member_dismiss_invite/${invId}`, {})
  }

  memberLeaveGroup(listingId: number): Observable<HttpResponse<any>> {
    return this.httpClient.delete<HttpResponse<any>>(`${this.baseUrl}/user_leave_group/${listingId}`)
  }
}
