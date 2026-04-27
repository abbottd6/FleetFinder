import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {
    ActiveRosterOptionsPanelComponent
} from "../active-roster-panel/active-roster-options-panel/active-roster-options-panel.component";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {MemberChipComponent} from "../member-chip/member-chip.component";
import {
  GroupManagementInteractService
} from "../../../../services/facade-services/group-management/group-management-interact.service";
import {BehaviorSubject, combineLatest, filter, map, Observable, Subject, takeUntil} from "rxjs";
import {
  GroupManagementUiPrefsService
} from "../../../../services/facade-services/group-management/group-management-ui-prefs/group-management-ui-prefs.service";
import {
  GroupListingFetchService
} from "../../../../services/api-services/group-listings-fetch-api/group-listing-fetch.service";
import {InviteActions, InviteWithActionInterface} from "../roster-invite-panel/invite-chip/invite-chip.component";
import {
  GroupManagementMemberViewModel
} from "../../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {RosterTabOptions} from "../roster-management.component";

export const WaitlistRosterActions = {
  MOVE_TO_ACTIVE: 'ACTIVE',
  REMOVE_WAITLIST_MEMBER: 'REMOVE',
  MESSAGE: 'MESSAGE',
} as const;

export type WaitlistRosterActions = typeof WaitlistRosterActions[keyof typeof WaitlistRosterActions];

export type WaitlistMemberWithActionInterface = {
  action: WaitlistRosterActions,
  member: GroupManagementMemberViewModel
}

export interface WaitlistPanelFilterState {
  terms: string | null;
}

@Component({
    selector: 'app-waitlist-roster-panel',
    standalone: true,
    templateUrl: './waitlist-roster-panel.component.html',
  imports: [
    ActiveRosterOptionsPanelComponent,
    AsyncPipe,
    MemberChipComponent,
    NgForOf,
    NgIf,
  ],
    styleUrl: './waitlist-roster-panel.component.css'
})
export class WaitlistRosterPanelComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() groupId!: number;

  waitlistFilterState$ = new BehaviorSubject<WaitlistPanelFilterState>({
    terms: null
  });

  protected waitlistMembersForDisplay$!: Observable<GroupManagementMemberViewModel[]>;

  constructor(protected managementInteract: GroupManagementInteractService,
              private mgmtUiPrefs: GroupManagementUiPrefsService,
              private listingFetch: GroupListingFetchService){}

  ngOnInit() {

    // uiPrefs does not store waitlist filters yet because only field is 'terms'
    // const tempFilterState = this.mgmtUiPrefs.storedWaitlistFilters;
    this.waitlistFilterState$.next({
      // ...tempFilterState,
      terms: null
    });

    this.waitlistMembersForDisplay$ = combineLatest([
      this.managementInteract.waitlistRoster$,
      this.waitlistFilterState$
    ]).pipe(
      filter(([members]) => !!members),
      map(([members, filterState]) =>
        this.filterWaitlistMembers(members.content, filterState))
    )
  }

  filterWaitlistMembers(members: GroupManagementMemberViewModel[], filterState: WaitlistPanelFilterState): GroupManagementMemberViewModel[] {
    if(filterState.terms == null) return members;

    const staticTerms = filterState.terms;

    return members.filter(member => {
      // const directionMatch = filterState.direction === 'BOTH' || WAITLIST_FILTER_PREDICATES[filterState.direction](invite);
      // const statusMatch = filterState.status === 'BOTH' || WAITLIST_FILTER_PREDICATES[filterState.status](invite);
      const termsMatch = member.userSummary.username.includes(staticTerms) || member.userSummary.inGameUsername.includes(staticTerms);
      return termsMatch;
    });
  }

  changeInviteStatus(inviteStatusChange: InviteWithActionInterface) {
    switch (inviteStatusChange.action) {

      case InviteActions.ACCEPT:
        this.managementInteract.acceptGroupInviteRequest(inviteStatusChange.invite);
        break;
      case InviteActions.DECLINE:
        this.managementInteract.declineGroupInviteRequest(inviteStatusChange.invite);
        break;
      case InviteActions.RESCIND:
        this.managementInteract.rescindGroupInviteOffer(inviteStatusChange.invite);
        break;
      case InviteActions.DISMISS:
        this.managementInteract.dismissGroupInvite(inviteStatusChange.invite);
        break;
      case InviteActions.WAITLIST:
        inviteStatusChange.invite.memberStatus = 'WAITLIST';
        this.managementInteract.waitlistMemberFromJoinRequest(inviteStatusChange.invite);
        break;
    }
  }

  catchFilterStateChange(state: WaitlistPanelFilterState) {
    this.waitlistFilterState$.next(state);
    // waitlist filter state is not stored yet because it only includes search terms
    // this.mgmtUiPrefs.saveInviteUiPrefs(state);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly RosterTabOptions = RosterTabOptions;
}
