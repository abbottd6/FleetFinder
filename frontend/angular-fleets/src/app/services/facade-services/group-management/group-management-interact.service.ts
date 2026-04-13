import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {
  GroupManagementMemberViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {
  GroupManagementInviteViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-management-invite-view-model";
import {Page} from "../../api-services/group-listings-fetch-api/group-listing-fetch.service";
import {
  GroupMembershipViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-membership-view-model";

@Injectable({
  providedIn: 'root'
})
export class GroupManagementInteractService {

  private activeRosterSubject: BehaviorSubject<Page<GroupManagementMemberViewModel> | undefined> =
    new BehaviorSubject<Page<GroupManagementMemberViewModel> | undefined>(undefined)
  public activeRoster$ = this.activeRosterSubject.asObservable();

  private waitlistRosterSubject: BehaviorSubject<Page<GroupManagementMemberViewModel> | undefined> =
    new BehaviorSubject<Page<GroupManagementMemberViewModel> | undefined>(undefined)
  public waitlistRoster$ = this.waitlistRosterSubject.asObservable();

  private groupInvitesSubject: BehaviorSubject<Page<GroupManagementInviteViewModel> | undefined> =
    new BehaviorSubject<Page<GroupManagementInviteViewModel> | undefined>(undefined)
  public groupInvites$ = this.groupInvitesSubject.asObservable();

  public sessionManager: GroupMembershipViewModel | undefined = undefined;

  constructor() { }

  setActiveRoster(roster: Page<GroupManagementMemberViewModel>) {
    this.activeRosterSubject.next(roster);
  }

  setWaitlistRoster(roster: Page<GroupManagementMemberViewModel>){
    this.waitlistRosterSubject.next(roster);
  }

  setGroupInvites(invites: Page<GroupManagementInviteViewModel>) {
    this.groupInvitesSubject.next(invites);
  }
}
