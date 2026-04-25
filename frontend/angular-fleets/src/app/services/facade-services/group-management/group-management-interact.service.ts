import {DestroyRef, inject, Injectable} from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {
  GroupManagementMemberViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {
  GroupManagementInviteViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-management-invite-view-model";
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
import {environment} from "../../../../environments/environment";
import {newEmptyPage, Page} from "../../../models/page-interface";
import {toTitleCase} from "../../../utils/global-functions";
import {ChatHostService} from "../chat/chat-host.service";

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
    new BehaviorSubject<Page<GroupManagementInviteViewModel>>(newEmptyPage());
  public groupInvites$ = this.groupInvitesSubject.asObservable();

  public sessionManager: GroupMembershipViewModel | undefined = undefined;

  constructor(private managementApi: MemberManagementApiService,
              private chatHostSrv: ChatHostService,
              protected dialog: MatDialog,
              private userService: UserService,
              private snackBar: MatSnackBar) {

  }

  setActiveRoster(roster: Page<GroupManagementMemberViewModel>) {
    this.activeRosterSubject.next(roster);
  }

  setWaitlistRoster(roster: Page<GroupManagementMemberViewModel>){
    this.waitlistRosterSubject.next(roster);
  }

  setGroupInvites(invites: Page<GroupManagementInviteViewModel>) {
    this.groupInvitesSubject.next(invites);
  }

  acceptGroupInviteRequest(inviteWithNewStatus: GroupManagementInviteViewModel) {
    this.managementApi.acceptGroupInviteRequest(inviteWithNewStatus).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (newMember: GroupManagementMemberViewModel) => {
          if(newMember.memberStatus === 'ACTIVE') {
            const current = this.activeRosterSubject.getValue() ?? newEmptyPage();
            this.activeRosterSubject.next({
              ...current,
              content: [...(current?.content ?? []), newMember]
            });
          } else if(newMember.memberStatus === 'WAITLIST') {
            const current = this.waitlistRosterSubject.getValue() ?? newEmptyPage();
            this.waitlistRosterSubject.next({
              ...current,
              content: [...(current?.content ?? []), newMember]
            });
          }

          this.spliceInviteSubjectForStatusChange(inviteWithNewStatus);

          const msg = `${inviteWithNewStatus.senderSummary.username} added to ${toTitleCase(newMember.memberStatus)}`;
          this.showSnackBarMessage(msg);
        },
        error: (e)=> {
          const msg = 'There was an issue adding this group member.';
          this.showSnackBarMessage(msg);
        }
      })
  }

  declineGroupInviteRequest(inviteWithNewStatus: GroupManagementInviteViewModel) {
    this.managementApi.declineGroupInviteRequest(inviteWithNewStatus.inviteId).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (declined: GroupManagementInviteViewModel) => {

          this.spliceInviteSubjectForStatusChange(declined);

          const msg =`Join request from ${inviteWithNewStatus.senderSummary.username} declined.`;
          this.showSnackBarMessage(msg);
        }
      })
  }

  openSendInvitePopup(sender: GroupMembershipViewModel, recipient: UserMonikerSummaryViewModel | null) {
    if(!this.userService.userLoggedIn) {
      const msg = 'You must be logged in to perform this action.';
      this.showSnackBarMessage(msg);
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
              const msg = 'Invite sent to ' + responseInv.recipientSummary.username;
              this.showSnackBarMessage(msg);

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
              if(err.status === 409) {
                const msg = 'This user is already a member of this group.';
                this.showSnackBarMessage(msg);
              } else {
                const msg = 'There was an error sending this invite.';
                this.showSnackBarMessage(msg);
                if (!environment.production) {
                  console.log(err.message);
                }
              }
            }
          });
      }
    });
  }

  rescindGroupInviteOffer(inviteWithNewStatus: GroupManagementInviteViewModel) {
    this.managementApi.rescindGroupInviteOffer(inviteWithNewStatus.inviteId).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (rescinded: GroupManagementInviteViewModel) => {

          this.spliceInviteSubjectForStatusChange(rescinded);

          const msg =`Group invite to ${inviteWithNewStatus.recipientSummary.username} rescinded.`;
          this.showSnackBarMessage(msg);
        },
        error: (e) => {
          if(e.status === 409){
            const msg = 'The status of this invite has been changed by another user already.';
            this.showSnackBarMessage(msg);
          } else {
            const msg = 'There was an error rescinding this invite.';
            this.showSnackBarMessage(msg);
          }
        }
      })
  }

  dismissGroupInvite(invite: GroupManagementInviteViewModel) {
    this.managementApi.managerDismissInvite(invite.inviteId).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          const current = this.groupInvitesSubject.getValue();
          const idx = current.content.findIndex(
            inv => inv.inviteId === invite.inviteId);
          this.groupInvitesSubject.next({
            ...current,
            content: [
              ...current.content.slice(0, idx),
              ...current.content.slice(idx + 1),
            ]
          })
        },
        error: (e) => {
          const msg = 'There was an error dismissing this invite.';
          this.showSnackBarMessage(msg);
        }
      })
  }

  openConversation(recipient: UserMonikerSummaryViewModel) {
    const title = this.sessionManager?.listing.listingTitle ?? 'Group Invite';
    this.chatHostSrv.provisionConversation(title, recipient.userId);
  }

  spliceInviteSubjectForStatusChange(inviteWithNewStatus: GroupManagementInviteViewModel) {
    const current = this.groupInvitesSubject.getValue() ?? newEmptyPage();
    const idx = current.content.findIndex(
      inv => inv.inviteId === inviteWithNewStatus.inviteId);
    this.groupInvitesSubject.next({
      ...current,
      content: [
        ...current.content.slice(0, idx),
        inviteWithNewStatus,
        ...current.content.slice(idx + 1)]
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
}
