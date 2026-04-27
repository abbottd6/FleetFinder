import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {BehaviorSubject, combineLatest, filter, map, Observable, Subject, takeUntil} from "rxjs";
import {
  MemberManagementApiService
} from "../../../../services/api-services/group-management/member-management-api.service";
import {
  GroupManagementInteractService
} from "../../../../services/facade-services/group-management/group-management-interact.service";
import {InviteOptionsPanelComponent} from "./invite-options-panel/invite-options-panel.component";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {InviteActions, InviteChipComponent, InviteWithActionInterface} from "./invite-chip/invite-chip.component";
import {
  GroupManagementInviteViewModel
} from "../../../../models/group-management-models/view-models/group-membership/group-management-invite-view-model";
import {
  GroupManagementUiPrefsService
} from "../../../../services/facade-services/group-management/group-management-ui-prefs/group-management-ui-prefs.service";
import {
  GroupListingFetchService
} from "../../../../services/api-services/group-listings-fetch-api/group-listing-fetch.service";
import {RosterTabOptions} from "../roster-management.component";

export interface InvitePanelFilterState {
  direction: 'OFFER' | 'REQUEST' | 'BOTH',
  status: 'PENDING' | 'ACTIONED' | 'BOTH',
  terms: string | null;
}

type InvitePredicate = (invite: GroupManagementInviteViewModel) => boolean;

const INVITE_FILTER_PREDICATES: Record<string, InvitePredicate> = {
  OFFER: (i) => i.inviteDirection === 'OFFER',
  REQUEST: (i) => i.inviteDirection === 'REQUEST',
  PENDING: (i) => i.inviteStatus === 'PENDING',
  ACTIONED: (i) => ['ACCEPTED', 'DECLINED', 'RESCINDED'].includes(i.inviteStatus),
};


@Component({
  selector: 'app-roster-invite-panel',
  standalone: true,
  templateUrl: './roster-invite-panel.component.html',
  imports: [
    InviteOptionsPanelComponent,
    AsyncPipe,
    InviteChipComponent,
    NgForOf,
  ],
  styleUrl: './roster-invite-panel.component.css'
})
export class RosterInvitePanelComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() groupId!: number;

  inviteFilterState$ = new BehaviorSubject<InvitePanelFilterState>({
    direction: 'BOTH', status: 'PENDING', terms: null
  });

  protected invitesForDisplay$!: Observable<GroupManagementInviteViewModel[]>;

  constructor(private memberManagementApi: MemberManagementApiService,
              protected managementInteract: GroupManagementInteractService,
              private mgmtUiPrefs: GroupManagementUiPrefsService,
              private listingFetch: GroupListingFetchService){}

  ngOnInit() {
    const tempFilterState = this.mgmtUiPrefs.storedInviteFilters;
    this.inviteFilterState$.next({
      ...tempFilterState,
      terms: null
    });

    this.invitesForDisplay$ = combineLatest([
      this.managementInteract.groupInvites$,
      this.inviteFilterState$
    ]).pipe(
      takeUntil(this.destroy$),
      filter(([invites]) => !!invites),
      map(([invites, filterState]) =>
        this.filterInvites(invites.content, filterState))
    )
  }

  filterInvites(invites: GroupManagementInviteViewModel[], filterState: InvitePanelFilterState): GroupManagementInviteViewModel[] {
    return invites.filter(invite => {
      const directionMatch = filterState.direction === 'BOTH' || INVITE_FILTER_PREDICATES[filterState.direction](invite);
      const statusMatch = filterState.status === 'BOTH' || INVITE_FILTER_PREDICATES[filterState.status](invite);
      return directionMatch && statusMatch;
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
        if(inviteStatusChange.invite.memberStatus === 'WAITLIST') {
          this.managementInteract.waitlistMemberFromJoinRequest(inviteStatusChange.invite);
        } else {
          this.managementInteract.mirrorActiveRequestToWaitlistInvite(inviteStatusChange.invite.inviteId);
        }
        break;
    }
  }

  catchFilterStateChange(state: InvitePanelFilterState) {
    this.inviteFilterState$.next(state);
    this.mgmtUiPrefs.saveInviteUiPrefs(state);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly RosterTabOptions = RosterTabOptions;
}
