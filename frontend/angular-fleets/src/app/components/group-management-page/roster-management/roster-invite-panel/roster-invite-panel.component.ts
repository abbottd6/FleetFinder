import {AfterViewInit, Component, ElementRef, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {
  BehaviorSubject,
  combineLatest,
  debounceTime,
  distinctUntilChanged,
  filter,
  map,
  Observable,
  Subject,
  takeUntil
} from "rxjs";
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
import {MatDialog} from "@angular/material/dialog";
import {ConfirmGenericComponent} from "../../../pop-ups/confirm-generic/confirm-generic.component";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {
  UserMonikerSummaryViewModel
} from "../../../../models/group-management-models/nested-models/user-moniker-summary-view-model";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {
  QuickAccessMenuService
} from "../../../../services/component-services/quick-access-menu/quick-access-menu.service";
import {MatRow} from "@angular/material/table";
import {
  MgmtInvitesQuickAccessMenuService
} from "../../../../services/component-services/group-management-quick-access-menus/mgmt-invites-quick-access-menu.service";
import {MatIcon} from "@angular/material/icon";
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatAutocompleteTrigger} from "@angular/material/autocomplete";
import {MatFormField, MatInput, MatLabel, MatSuffix} from "@angular/material/input";
import {MatIconButton} from "@angular/material/button";
import {RosterTextFieldFilterComponent} from "../roster-text-field-filter/roster-text-field-filter.component";

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
    MatMenu,
    MatMenuItem,
    NgIf,
    MatMenuTrigger,
    FormsModule,
    ReactiveFormsModule,
    RosterTextFieldFilterComponent,
    MatIcon,
  ],
  styleUrl: './roster-invite-panel.component.css'
})
export class RosterInvitePanelComponent implements OnInit, AfterViewInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() groupId!: number;
  @ViewChild(MatMenuTrigger) menuTrigger!: MatMenuTrigger;
  @ViewChild('contextMenuAnchor', { read: ElementRef }) protected contextMenuAnchor!: ElementRef<HTMLElement>;

  protected filterTermsCtrl = new FormControl<string | null>(null);

  inviteFilterState$ = new BehaviorSubject<InvitePanelFilterState>({
    direction: 'BOTH', status: 'PENDING', terms: null
  });

  protected invitesForDisplay$!: Observable<GroupManagementInviteViewModel[]>;

  constructor(protected managementInteract: GroupManagementInteractService,
              private mgmtUiPrefs: GroupManagementUiPrefsService,
              private listingFetch: GroupListingFetchService,
              protected invitesQuickMenu: MgmtInvitesQuickAccessMenuService){}

  ngOnInit() {
    const tempFilterState = this.mgmtUiPrefs.storedInviteFilters;
    this.inviteFilterState$.next({
      ...tempFilterState,
      terms: null
    });

    this.filterTermsCtrl.valueChanges.pipe(
      takeUntil(this.destroy$),
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(() => {
      this.setAndFilterByTerms();
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

  ngAfterViewInit() {
    this.invitesQuickMenu.registerMenu(this.menuTrigger, this.contextMenuAnchor);
  }

  filterInvites(invites: GroupManagementInviteViewModel[], filterState: InvitePanelFilterState): GroupManagementInviteViewModel[] {
    return invites.filter(invite => {
      const directionMatch = filterState.direction === 'BOTH' || INVITE_FILTER_PREDICATES[filterState.direction](invite);
      const statusMatch = filterState.status === 'BOTH' || INVITE_FILTER_PREDICATES[filterState.status](invite);
      const termsMatch = !filterState.terms || (
        invite.inviteDirection === 'OFFER'
        ? (invite.recipientSummary.username.includes(filterState.terms) || invite.recipientSummary.inGameUsername.includes(filterState.terms))
        : (invite.senderSummary.username.includes(filterState.terms) || invite.senderSummary.inGameUsername.includes(filterState.terms))
      )
      return directionMatch && statusMatch && termsMatch;
    });
  }

  setAndFilterByTerms() {
    const current = this.inviteFilterState$.getValue();
    this.inviteFilterState$.next({
      ...current,
      terms: this.filterTermsCtrl.value
    })
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
      case InviteActions.BLOCK:
        this.managementInteract.openConfirmBlockUser(inviteStatusChange.invite);
    }
  }

  changeInviteStatusFromQuckMenu(action: InviteActions, invite: GroupManagementInviteViewModel) {
    invite.inviteStatus = action;
    const inviteChange: InviteWithActionInterface = { action: action, invite: invite};

    this.changeInviteStatus(inviteChange);
  }

  altInviteActionFromQuickMenu(action: InviteActions, invite: GroupManagementInviteViewModel) {
    const inviteChange: InviteWithActionInterface = { action: action, invite: invite};

    this.changeInviteStatus(inviteChange);
  }

  catchFilterStateChange(state: InvitePanelFilterState) {
    this.inviteFilterState$.next(state);
    // this.mgmtUiPrefs.saveInviteUiPrefs(state);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly RosterTabOptions = RosterTabOptions;
  protected readonly InviteActions = InviteActions;
}
