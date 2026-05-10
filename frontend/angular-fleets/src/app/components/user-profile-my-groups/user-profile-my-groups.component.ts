import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input, OnChanges,
  OnDestroy,
  OnInit,
  Output, SimpleChanges,
} from '@angular/core';
import {
  GroupMembershipViewModel
} from "../../models/group-management-models/view-models/group-membership/group-membership-view-model";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {
  GroupMembershipsInteractService
} from "../../services/facade-services/group-management/group-memberships-interact.service";
import {BehaviorSubject, combineLatest, filter, map, Observable, Subject, takeUntil} from "rxjs";
import {GroupsChipComponent} from "./groups-chip/groups-chip.component";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {UiPrefsService} from "../../services/facade-services/ui-prefs/ui-prefs.service";
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelDescription,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";
import {
  GroupInviteViewModel
} from "../../models/group-management-models/view-models/group-membership/group-invite-view-model";
import {GroupMembershipApiService} from "../../services/api-services/group-membership-api/group-membership-api.service";
import {newEmptyPage, Page} from "../../models/page-interface";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MemberInviteActionInterface, MyInvitesChipComponent} from "./my-invite-chip/my-invites-chip.component";
import {
  InviteActions
} from "../group-management-page/roster-management/roster-invite-panel/invite-chip/invite-chip.component";
import {
  InvitePanelFilterState
} from "../group-management-page/roster-management/roster-invite-panel/roster-invite-panel.component";
import {ChatHostService} from "../../services/facade-services/chat/chat-host.service";
import {MatRadioButton, MatRadioGroup} from "@angular/material/radio";
import {MatDialog} from "@angular/material/dialog";
import {
  AcceptInviteOfferPopupFormComponent, UserAcceptInviteOfferFormData
} from "./my-invite-chip/accept-invite-offer-popup-form/accept-invite-offer-popup-form.component";
import {UserService} from "../../services/user-services/user.service";

type InvitePredicate = (invite: GroupInviteViewModel) => boolean;

const INVITE_FILTER_PREDICATES: Record<string, InvitePredicate> = {
  OFFER: (i) => i.inviteDirection === 'OFFER',
  REQUEST: (i) => i.inviteDirection === 'REQUEST',
  PENDING: (i) => i.inviteStatus === 'PENDING',
  ACTIONED: (i) => ['ACCEPTED', 'DECLINED', 'RESCINDED'].includes(i.inviteStatus),
};

export interface MembershipProfileFilterState {
  groupStatus: 'PAST' | 'UPCOMING' | 'BOTH',
  managementPrivileges: 'PRIVILEGES' | 'NO_PRIVILEGES' | 'BOTH',
  ownership: 'OWNER' | 'NOT_OWNER' | 'BOTH',
  roster: 'ACTIVE' | 'WAITLIST' | 'BOTH',
}

type MembershipPredicate = (membership: GroupMembershipViewModel) => boolean;

const MEMBERSHIP_FILTER_PREDICATES: Record<string, MembershipPredicate> = {
  UPCOMING: (m) => {
    const now = Date.now();
    const oneHourAgo = now - 60 * 60 * 1000;
    const eventTime = m.listing.eventSchedule ? new Date(m.listing.eventSchedule).getTime() : new Date(m.listing.creationTimestamp).getTime();

    return (eventTime > oneHourAgo)
  },
  PAST: (m) => {
    const now = Date.now();
    const oneHourAgo = now - 60 * 60 * 1000;
    const eventTime = m.listing.eventSchedule ? new Date(m.listing.eventSchedule).getTime() : new Date(m.listing.creationTimestamp).getTime();

    return (eventTime < oneHourAgo);
  },
  PRIVILEGES: (m) => m.isAuthorizedManager,
  NO_PRIVILEGES: (m) => (!m.isAuthorizedManager),
  OWNER: (m) => m.memberRank.rankTitle === 'Owner',
  NOT_OWNER: (m) => m.memberRank.rankTitle !== 'Owner',
  ACTIVE: (m) => m.memberStatus === 'ACTIVE',
  WAITLIST: (m) => m.memberStatus === 'WAITLIST',
};

export const MembershipSortFields = {
  joinedAt: 'joinedAt',
  nearest: 'nearest'
};

export type MembershipSortFields = (typeof MembershipSortFields)[keyof typeof MembershipSortFields];


@Component({
  selector: 'app-user-profile-my-groups',
  standalone: true,
  templateUrl: './user-profile-my-groups.component.html',
  imports: [
    NgIf,
    AsyncPipe,
    NgForOf,
    GroupsChipComponent,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatExpansionPanelDescription,
    MatAccordion,
    MyInvitesChipComponent,
    MatRadioButton,
    MatRadioGroup,
  ],
  styleUrl: './user-profile-my-groups.component.css'
})
export class UserProfileMyGroupsComponent implements OnInit, AfterViewInit, OnChanges, OnDestroy {
  private destroy$ = new Subject<void>();

  protected myInvitesSubject$ = new BehaviorSubject<GroupInviteViewModel[]>([]);
  protected myInvitesForDisplay$ = new Observable<GroupInviteViewModel[]>
  protected inviteFilterState$ = new BehaviorSubject<InvitePanelFilterState>({
    direction: 'BOTH', status: 'BOTH', terms: null
  });

  protected myMembershipsForDisplay$ = new Observable<GroupMembershipViewModel[]>;
  protected membershipFilterState$ = new BehaviorSubject<MembershipProfileFilterState>({
    groupStatus: 'UPCOMING', ownership: 'BOTH', managementPrivileges: 'BOTH', roster: 'BOTH'
  })

  @Input() routeSubsectionSelect?: string;
  groupsSubsections = ['invites', 'memberships'];

  protected invitesExpanded: boolean = false;
  protected membershipsExpanded: boolean = true;

  protected invIdx: number = 0;
  protected invSize: number = 10;
  protected invTotalElements: number = 0;
  protected invTotalPages: number = 0;
  protected noInvites: boolean = true;

  @Output() emitListing = new EventEmitter<GroupListingViewModel>();
  constructor(protected memberInteract: GroupMembershipsInteractService,
              private uiPrefs: UiPrefsService,
              private memberApi: GroupMembershipApiService,
              private snackBar: MatSnackBar,
              private chatHostSrv: ChatHostService,
              private dialog: MatDialog,
              private userService: UserService) {}

  ngOnInit() {
    this.memberInteract.getMyGroupMemberships();
    this.getMyGroupInvites();
    this.uiPrefs.loadUiPrefs();

    this.myInvitesForDisplay$ = combineLatest([
      this.myInvitesSubject$,
      this.inviteFilterState$
    ]).pipe(
      takeUntil(this.destroy$),
      filter(([invites]) => !!invites),
      map(([invites, filterState]) =>
        this.filterInvites(invites, filterState))
    )

    this.myMembershipsForDisplay$ = combineLatest([
      this.memberInteract.groupMemberships$,
      this.membershipFilterState$
    ]).pipe(
      takeUntil(this.destroy$),
      filter(([memberships]) => !!memberships),
      map(([memberships, filterState]) =>
        this.filterMemberships(memberships, filterState))
    )
  }

  ngAfterViewInit() {
    if(this.routeSubsectionSelect) {
      setTimeout(() => this.scrollToSection(this.routeSubsectionSelect), 300);
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if(changes['routeSubsectionSelect']?.currentValue === 'invites') {
      this.invitesExpanded = true;
      this.membershipsExpanded = false;
    } else if(changes['routeSubsectionSelect']?.currentValue === 'memberships') {
      this.membershipsExpanded = true;
    }
  }

  passListingEmissionToParent(listing: GroupListingViewModel) {
    this.emitListing.emit(listing);
  }

  getMyGroupInvites() {
    this.memberApi.getMyGroupInvites(this.invIdx, this.invSize).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (invites: Page<GroupInviteViewModel>) => {
          const current = this.myInvitesSubject$.getValue() ?? newEmptyPage();
          this.myInvitesSubject$.next([
            ...current,
            ...invites.content
          ]);
          this.invIdx = invites.page.number;
          this.invSize = invites.page.size;
          this.invTotalElements = invites.page.totalElements;
          this.invTotalPages = invites.page.totalPages;
          this.noInvites = invites.content.length === 0;
        },
        error: (e) => {
          const msg = 'There was an error retrieving your invites, try refreshing the page.';
          this.showSnackBarMessage(msg);
        }
      })
  }

  filterInvites(invites: GroupInviteViewModel[], filterState: InvitePanelFilterState): GroupInviteViewModel[] {
    return invites.filter(invite => {
      const directionMatch = filterState.direction === 'BOTH' || INVITE_FILTER_PREDICATES[filterState.direction](invite);
      const statusMatch = filterState.status === 'BOTH' || INVITE_FILTER_PREDICATES[filterState.status](invite);
      return directionMatch && statusMatch;
    });
  }

  filterMemberships(memberships: GroupMembershipViewModel[], filterState: MembershipProfileFilterState): GroupMembershipViewModel[] {
    return memberships.filter(member => {

      const statusMatch = filterState.groupStatus === 'BOTH' ||
        MEMBERSHIP_FILTER_PREDICATES[filterState.groupStatus](member);

      const privilegeMatch = filterState.managementPrivileges === 'BOTH' ||
        MEMBERSHIP_FILTER_PREDICATES[filterState.managementPrivileges](member);

      const ownershipMatch = filterState.ownership === 'BOTH' ||
        MEMBERSHIP_FILTER_PREDICATES[filterState.ownership](member);

      const rosterMatch = filterState.roster === 'BOTH' ||
        MEMBERSHIP_FILTER_PREDICATES[filterState.roster](member);

      return statusMatch && privilegeMatch && ownershipMatch && rosterMatch;
    })
  }

  sortMemberships(field: MembershipSortFields) {
    this.memberInteract.sortMembershipsPage(field);
  }

  getMoreInvites() {
    this.invIdx++
    this.getMyGroupInvites();
  }

  getMoreMemberships() {
    this.memberInteract.membershipIdx++
    this.memberInteract.getMyGroupMemberships();
  }

  handleInviteChipAction(actionInvite: MemberInviteActionInterface) {
    const invId = actionInvite.invite.inviteId;


    switch (actionInvite.action) {

      case InviteActions.ACCEPT:

        const dialogRef = this.dialog.open(AcceptInviteOfferPopupFormComponent, {
          data: {
            listing: actionInvite.invite.listingDetails,
            inGameUsername: this.userService.inGameUsername
          }
        });

        dialogRef.afterClosed().pipe(takeUntil(this.destroy$))
          .subscribe((formData: UserAcceptInviteOfferFormData) => {
            if(formData) {

              actionInvite.invite.inviteStatus = InviteActions.ACCEPT;

              actionInvite.invite.hasMic = formData.hasMic;
              actionInvite.invite.hasHeadset = formData.hasHeadset;
              actionInvite.invite.recipientSummary.inGameUsername = formData.inGameUsername;

              this.memberApi.acceptGroupInviteOffer(actionInvite.invite).pipe(takeUntil(this.destroy$))
                .subscribe({
                  next: (newMembership: GroupMembershipViewModel) => {
                    this.reinsertUpdatedInvite(actionInvite.invite);
                    this.memberInteract.addAcceptedInviteNewMembership(newMembership);
                    const msg = `Accepted ${actionInvite.invite.senderSummary.username }'s group invite.`;
                    this.showSnackBarMessage(msg);
                  },
                  error: (e) => {
                    if(e.status === 409) {
                      const msg = 'You are already a member of this group or someone has already modified this invite';
                      this.showSnackBarMessage(msg);
                    } else {
                      const msg = 'There was an error accepting this invite';
                      this.showSnackBarMessage(msg);
                    }
                  }
                });
            }
          });
        break;

      case InviteActions.DECLINE:

        this.memberApi.declineGroupInviteOffer(invId).pipe(takeUntil(this.destroy$))
          .subscribe({
            next: (inviteNewStatus: GroupInviteViewModel) => {
              this.reinsertUpdatedInvite(inviteNewStatus);
              const msg = `Declined ${ inviteNewStatus.senderSummary.username }'s group invite.`;
              this.showSnackBarMessage(msg);
            },
            error: (e) => {
              if(e.status === 409) {
                const msg = 'Someone has already modified the status of this invite';
                this.showSnackBarMessage(msg);
              } else {
                const msg = 'There was an error declining this group invite';
                this.showSnackBarMessage(msg);
              }
            }
          });
        break;

      case InviteActions.RESCIND:

        this.memberApi.rescindJoinRequest(invId).pipe(takeUntil(this.destroy$))
          .subscribe({
            next: (inviteNewStatus: GroupInviteViewModel) => {
              this.reinsertUpdatedInvite(inviteNewStatus);
              const msg = `You rescinded the join request for ${inviteNewStatus.recipientSummary.username}'s group`;
              this.showSnackBarMessage(msg);
            },
            error: (e) => {
              if(e.status === 409) {
                const msg = 'Someone has already changed the status of your join request.';
                this.showSnackBarMessage(msg);
              } else {
                const msg = 'There was an error rescinding this join request.'
                this.showSnackBarMessage(msg);
              }
            }
          });
        break;

      case InviteActions.DISMISS:

        this.memberApi.dismissActionedInvite(actionInvite.invite.inviteId).pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              const current = this.myInvitesSubject$.getValue();
              const idx = current.findIndex(inv => inv.inviteId === actionInvite.invite.inviteId);
              this.myInvitesSubject$.next([
                ...current.slice(0, idx),
                ...current.slice(idx + 1)
              ])
            }
          });
        break;

      case InviteActions.MESSAGE:
        const title = actionInvite.invite.listingDetails.listingTitle;

        if(actionInvite.invite.inviteDirection === 'OFFER') {
          this.chatHostSrv.provisionConversation(title, actionInvite.invite.senderSummary.userId);
        } else  {
          this.chatHostSrv.provisionConversation(title, actionInvite.invite.recipientSummary.userId);
        }
    }
  }

  //INVITE FILTERS

  onStatusFilterChange(status: InvitePanelFilterState['status']) {
    this.inviteFilterState$.next({
      ...this.inviteFilterState$.getValue(),
      status
    })
  }

  onDirectionFilterChange(direction: InvitePanelFilterState['direction']) {
    this.inviteFilterState$.next({
      ...this.inviteFilterState$.getValue(),
      direction
    })
  }

  //MEMBERSHIP FILTERS

  onMembershipGroupScheduleFilterChange(groupStatus: MembershipProfileFilterState['groupStatus']) {
    this.membershipFilterState$.next({
      ...this.membershipFilterState$.getValue(),
      groupStatus
    })
  }

  onMembershipPrivilegesFilterChange(managementPrivileges: MembershipProfileFilterState['managementPrivileges']) {
    this.membershipFilterState$.next({
      ...this.membershipFilterState$.getValue(),
      managementPrivileges
    })
  }

  onMembershipOwnershipFilterChange(ownership: MembershipProfileFilterState['ownership']) {
    this.membershipFilterState$.next({
      ...this.membershipFilterState$.getValue(),
      ownership
    })
  }

  onMembershipRosterFilterChange(roster: MembershipProfileFilterState['roster']) {
    this.membershipFilterState$.next({
      ...this.membershipFilterState$.getValue(),
      roster
    })
  }

  showSnackBarMessage(message: string) {
    this.snackBar.open(`${message}`, 'OK', {
      duration: 4000,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['mobile-snackbar']
    })
  }

  reinsertUpdatedInvite(updated: GroupInviteViewModel) {
    const currentInvs = this.myInvitesSubject$.getValue();
    const idx = currentInvs.findIndex(inv => inv.inviteId === updated.inviteId);

    this.myInvitesSubject$.next([
      ...currentInvs.slice(0, idx),
      updated,
      ...currentInvs.slice(idx + 1)
    ])
  }

  scrollToSection(section: string | undefined) {
    if (!section) return;

    if(this.groupsSubsections.includes(section)) {
      document.getElementById(section)?.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      })
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
