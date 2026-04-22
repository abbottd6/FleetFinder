import {DestroyRef, inject, Injectable} from '@angular/core';
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
import {MatDialog} from "@angular/material/dialog";
import {
  SendGroupInvitePopupComponent
} from "../../../components/pop-ups/send-group-invite-popup/send-group-invite-popup.component";
import {
  UserMonikerSummaryViewModel
} from "../../../models/group-management-models/nested-models/user-moniker-summary-view-model";
import {UserService} from "../../user-services/user.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {SendGroupInviteOffer} from "../../../models/group-management-models/request-models/send-group-invite-offer";
import {MemberManagementApiService} from "../../api-services/group-management/member-management-api.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

@Injectable({
  providedIn: 'root'
})
export class GroupManagementInteractService {

  private destroyRef = inject(DestroyRef);

  private activeRosterSubject: BehaviorSubject<Page<GroupManagementMemberViewModel> | undefined> =
    new BehaviorSubject<Page<GroupManagementMemberViewModel> | undefined>(undefined)
  public activeRoster$ = this.activeRosterSubject.asObservable();

  private waitlistRosterSubject: BehaviorSubject<Page<GroupManagementMemberViewModel> | undefined> =
    new BehaviorSubject<Page<GroupManagementMemberViewModel> | undefined>(undefined)
  public waitlistRoster$ = this.waitlistRosterSubject.asObservable();

  private groupInvitesSubject: BehaviorSubject<Page<GroupManagementInviteViewModel>> =
    new BehaviorSubject<Page<GroupManagementInviteViewModel>>({
      content: [],
      page: {
        size: 0,
        number: 0,
        totalElements: 0,
        totalPages: 0,
      },
      sort: {
        empty: true,
        sorted: false,
        unsorted: true,
        asc: false,
        desc: true
      }
    });
  public groupInvites$ = this.groupInvitesSubject.asObservable();

  public sessionManager: GroupMembershipViewModel | undefined = undefined;

  constructor(private managementApi: MemberManagementApiService,
              protected dialog: MatDialog,
              private userService: UserService,
              private snackBar: MatSnackBar) { }

  setActiveRoster(roster: Page<GroupManagementMemberViewModel>) {
    this.activeRosterSubject.next(roster);
  }

  setWaitlistRoster(roster: Page<GroupManagementMemberViewModel>){
    this.waitlistRosterSubject.next(roster);
  }

  setGroupInvites(invites: Page<GroupManagementInviteViewModel>) {
    this.groupInvitesSubject.next(invites);
  }

  openSendInvitePopup(sender: GroupMembershipViewModel, recipient: UserMonikerSummaryViewModel | null) {
    if(!this.userService.userLoggedIn) {
      this.snackBar.open('You must be logged in to perform this action.', 'OK', {
        duration: 4000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['mobile-snackbar']
      })
      return;
    }

    const dialogRef = this.dialog.open(SendGroupInvitePopupComponent, {
      disableClose: true,
      data: {
        listing: sender.listing,
        recipientSummary: recipient,
      }
    });

    dialogRef.afterClosed().subscribe((invite: SendGroupInviteOffer | null) => {
      if(invite) {
        this.managementApi.sendGroupInviteOffer(invite).pipe(takeUntilDestroyed(this.destroyRef))
          .subscribe({
            next: (responseInv: GroupManagementInviteViewModel)=> {
              this.snackBar.open('Invite sent to ' + responseInv.recipientSummary.username, 'OK', {
                duration: 4000,
                verticalPosition: 'top',
                horizontalPosition: 'center',
                panelClass: ['mobile-snackbar']
              })

              const current = this.groupInvitesSubject.getValue();
              if(current) {
                this.groupInvitesSubject.next({
                  ...current,
                  content: [...current.content, responseInv]
                });
              } else {
                this.groupInvitesSubject.next({
                  content: [responseInv],
                  page: {
                    size: 1,
                    number: 0,
                    totalElements: 1,
                    totalPages: 1,
                  },
                  sort: {
                    empty: true,
                    sorted: false,
                    unsorted: true,
                    asc: false,
                    desc: true,
                  },

                })
              }
            },
            error: (err) => {
              this.snackBar.open('There was an error sending this invite.', 'OK', {
                duration: 5000,
                verticalPosition: 'top',
                horizontalPosition: 'center',
                panelClass: ['mobile-snackbar']
              })

              console.log(err.message);
            }
          });
      }
    });
  }
}
