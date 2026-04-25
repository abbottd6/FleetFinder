import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {
  GroupMembershipViewModel
} from "../../models/group-management-models/view-models/group-membership/group-membership-view-model";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {
  GroupMembershipsInteractService
} from "../../services/facade-services/group-management/group-memberships-interact.service";
import {BehaviorSubject, Subject, takeUntil} from "rxjs";
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
  InviteStatus
} from "../../services/facade-services/group-management/group-management-ui-prefs/group-management-ui-prefs.service";
import {
  InviteActions
} from "../group-management-page/roster-management/roster-invite-panel/invite-chip/invite-chip.component";
import {MatRadioButton, MatRadioGroup} from "@angular/material/radio";
import {
  InvitePanelFilterState
} from "../group-management-page/roster-management/roster-invite-panel/roster-invite-panel.component";
import {
  InviteOptionsPanelComponent
} from "../group-management-page/roster-management/roster-invite-panel/invite-options-panel/invite-options-panel.component";

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
    InviteOptionsPanelComponent
  ],
  styleUrl: './user-profile-my-groups.component.css'
})
export class UserProfileMyGroupsComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  protected myInvitesSubject$ = new BehaviorSubject<GroupInviteViewModel[]>([]);
  protected inviteFilterState$ = new BehaviorSubject<InvitePanelFilterState>({
    direction: 'BOTH', status: 'BOTH', terms: null
  });


  protected invIdx: number = 0;
  protected invSize: number = 10;
  protected invTotalElements: number = 0;
  protected invTotalPages: number = 0;
  protected noInvites: boolean = true;

  @Output() emitListing = new EventEmitter<GroupListingViewModel>();
  constructor(protected memberInteract: GroupMembershipsInteractService,
              private uiPrefs: UiPrefsService,
              private memberApi: GroupMembershipApiService,
              private snackBar: MatSnackBar) {}

  ngOnInit() {
    this.memberInteract.getMyGroupMemberships();
    this.getMyGroupInvites();
    this.uiPrefs.loadUiPrefs();
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

  getMoreInvites() {
    this.invIdx++
    this.getMyGroupInvites();
  }

  handleInviteChipAction(actionInvite: MemberInviteActionInterface) {
    const invId = actionInvite.invite.inviteId;

    switch (actionInvite.action) {
      case InviteActions.ACCEPT:
        actionInvite.invite.inviteStatus = InviteActions.ACCEPT;
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
    }
  }

  catchFilterStateChange(state: InvitePanelFilterState) {
    this.inviteFilterState$.next(state);
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

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
