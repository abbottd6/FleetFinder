import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {BehaviorSubject, combineLatest, filter, map, Observable, Subject, switchMap, take, takeUntil} from "rxjs";
import {
  MemberManagementApiService
} from "../../../../services/api-services/group-management/member-management-api.service";
import {
  GroupManagementInteractService
} from "../../../../services/facade-services/group-management/group-management-interact.service";
import {InviteOptionsPanelComponent} from "./invite-options-panel/invite-options-panel.component";
import {AsyncPipe, NgForOf} from "@angular/common";
import {InviteActions, InviteChipComponent, InviteWithActionInterface} from "./invite-chip/invite-chip.component";
import {
  GroupManagementInviteViewModel
} from "../../../../models/group-management-models/view-models/group-membership/group-management-invite-view-model";
import {
  GroupManagementUiPrefsService
} from "../../../../services/facade-services/group-management/group-management-ui-prefs/group-management-ui-prefs.service";
import {ConversationProvisionInterface} from "../../../../services/facade-services/chat/chat-host.service";
import {
  GroupListingFetchService
} from "../../../../services/api-services/group-listings-fetch-api/group-listing-fetch.service";
import {GroupListingViewModel} from "../../../../models/group-listing/group-listing-view-model";

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
    NgForOf
  ],
  styleUrl: './roster-invite-panel.component.css'
})
export class RosterInvitePanelComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  protected groupId!: number;

  protected noGroupInvites: boolean = true;

  inviteFilterState$ = new BehaviorSubject<InvitePanelFilterState>({
    direction: 'BOTH', status: 'PENDING', terms: null
  });

  protected invitesForDisplay$!: Observable<GroupManagementInviteViewModel[]>;

  constructor(private memberManagementApi: MemberManagementApiService,
              protected managementInteract: GroupManagementInteractService,
              private mgmtUiPrefs: GroupManagementUiPrefsService,
              private listingFetch: GroupListingFetchService){}

  ngOnInit() {
    if(this.managementInteract.sessionManager) {
      this.groupId = this.managementInteract.sessionManager?.listing.groupId;
    }

    this.memberManagementApi.getGroupInvites(this.groupId).pipe(takeUntil(this.destroy$))
      .subscribe(page => {
        this.managementInteract.setGroupInvites(page);
        this.noGroupInvites = page.content.length === 0;
      });

    const tempFilterState = this.mgmtUiPrefs.storedInviteFilters;
    this.inviteFilterState$.next({
      ...tempFilterState,
      terms: null
    });

    this.invitesForDisplay$ = combineLatest([
      this.managementInteract.groupInvites$,
      this.inviteFilterState$
    ]).pipe(
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
      case 'ACCEPTED':
        this.managementInteract.acceptGroupInviteRequest(inviteStatusChange.invite);
        break;
      case 'DECLINED':
        this.managementInteract.declineGroupInviteRequest(inviteStatusChange.invite);
        break;
      case 'RESCINDED':
        this.managementInteract.rescindGroupInviteOffer(inviteStatusChange.invite);
        break;
      case 'DISMISS':
        this.managementInteract.dismissGroupInvite(inviteStatusChange.invite);
        break;
      case 'WAITLIST':
        //TODO implement
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
}
