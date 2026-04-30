import {DestroyRef, inject, Injectable} from '@angular/core';
import {BehaviorSubject, takeUntil} from "rxjs";
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
import {ConfirmGenericComponent} from "../../../components/pop-ups/confirm-generic/confirm-generic.component";

@Injectable({
  providedIn: 'root'
})
export class GroupManagementInteractService {

  private destroyRef = inject(DestroyRef);

  private activeRosterSubject: BehaviorSubject<Page<GroupManagementMemberViewModel>> =
    new BehaviorSubject<Page<GroupManagementMemberViewModel>>(newEmptyPage());
  public activeRoster$ = this.activeRosterSubject.asObservable();

  private waitlistRosterSubject: BehaviorSubject<Page<GroupManagementMemberViewModel>> =
    new BehaviorSubject<Page<GroupManagementMemberViewModel>>(newEmptyPage());
  public waitlistRoster$ = this.waitlistRosterSubject.asObservable();

  private groupInvitesSubject: BehaviorSubject<Page<GroupManagementInviteViewModel>> =
    new BehaviorSubject<Page<GroupManagementInviteViewModel>>(newEmptyPage());
  public groupInvites$ = this.groupInvitesSubject.asObservable();

  public sessionManager: GroupMembershipViewModel | undefined = undefined;

  private selectedInviteSubject = new BehaviorSubject<GroupManagementInviteViewModel | null>(null);
  public selectedInvite$ = this.selectedInviteSubject.asObservable();


  constructor(private managementApi: MemberManagementApiService,
              private chatHostSrv: ChatHostService,
              protected dialog: MatDialog,
              private userService: UserService,
              private snackBar: MatSnackBar) {

  }

  setActiveRoster(roster: Page<GroupManagementMemberViewModel>) {
    this.activeRosterSubject.next(roster);
  }

  fetchActiveRoster(groupId: number) {
    this.managementApi.getActiveRosterGroupMembers(groupId).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(page => {
        this.setActiveRoster(page);
      });
  }

  setWaitlistRoster(roster: Page<GroupManagementMemberViewModel>){
    this.waitlistRosterSubject.next(roster);
  }

  fetchWaitlistRoster(groupId: number) {
    this.managementApi.getWaitListMembers(groupId).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(page => {
        this.setWaitlistRoster(page);
      })
  }

  setGroupInvites(invites: Page<GroupManagementInviteViewModel>) {
    this.groupInvitesSubject.next(invites);
  }

  fetchGroupInvites(groupId: number) {
    this.managementApi.getGroupInvites(groupId).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(page => {
        this.setGroupInvites(page);
      })
  }

  setSelectedInvite(inv: GroupManagementInviteViewModel) {
    this.selectedInviteSubject.next(inv);
  }

  clearSelectedInvite() {
    this.selectedInviteSubject.next(null);
  }

  acceptGroupInviteRequest(acceptedInvite: GroupManagementInviteViewModel) {
    this.managementApi.newMemberFromJoinRequest(acceptedInvite).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (newMember: GroupManagementMemberViewModel) => {
            const current = this.activeRosterSubject.getValue() ?? newEmptyPage();
            this.activeRosterSubject.next({
              ...current,
              content: [...(current?.content ?? []), newMember]
            });

          this.spliceInviteSubjectForStatusChange(acceptedInvite);

          const msg = `${acceptedInvite.senderSummary.username} added to ${toTitleCase(newMember.memberStatus)}`;
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

  openConfirmBlockUser(inviteRequest: GroupManagementInviteViewModel) {
    const dialogRef = this.dialog.open(ConfirmGenericComponent, {
      data: {
        title: "Block join requests from " + inviteRequest.senderSummary.username + "?",
        message: "Blocking requests from this user will only apply to this group. The user will be unable to send new " +
          "join requests. If you would also like to mute this user's chat messages, you will need to do this from the" +
          "chat panel by right clicking (long press on mobile) on the conversation with the user and selecting mute."
      }
    })

    dialogRef.afterClosed().pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(result => {
        if(result) {
          this.managementApi.blockInviteRequests(inviteRequest.inviteId).pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
              next: (blocked: UserMonikerSummaryViewModel) => {
                const message = blocked.username + " has been blocked.";
                this.showSnackBarMessage(message);

                const currentInvs = this.groupInvitesSubject.getValue();
                const idx = currentInvs.content.findIndex(inv =>
                  inv.inviteId === inviteRequest.inviteId);
                this.groupInvitesSubject.next({
                  ...currentInvs,
                  content: [
                    ...currentInvs.content.slice(0, idx),
                    ...currentInvs.content.slice(idx + 1)
                  ]
                })
              },
              error: (e) => {
                if(e.status === 409) {
                  this.showSnackBarMessage("User could not be blocked because the status of one of their invites has changed.")
                } else {
                  this.showSnackBarMessage("There was an issue blocking this user.")
                }
              }
            })
        }
      })
  }

  mirrorActiveRequestToWaitlistInvite(invId: number) {
    this.managementApi.mirrorActiveRequestToWaitlistInvite(invId).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (inviteListingId) => {
          this.fetchGroupInvites(inviteListingId);
          this.showSnackBarMessage('New Waitlist invite sent to user');
        },
        error: (e) => {
          this.showSnackBarMessage('There was an error. Try creating a new Waitlist invite instead.');
        }
      })
  }

  waitlistMemberFromJoinRequest(waitlistInvite: GroupManagementInviteViewModel) {
    this.managementApi.newMemberFromJoinRequest(waitlistInvite).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (newMember: GroupManagementMemberViewModel) => {
          const current = this.waitlistRosterSubject.getValue() ?? newEmptyPage();
          this.waitlistRosterSubject.next({
            ...current,
            content: [...(current?.content ?? []), newMember]
          });

          this.spliceInviteSubjectForStatusChange(waitlistInvite);

          const msg = `${waitlistInvite.senderSummary.username} added to ${toTitleCase(newMember.memberStatus)}`;
          this.showSnackBarMessage(msg);
        },
        error: (e)=> {
          const msg = `There was an issue adding ${waitlistInvite.senderSummary.username} to the waitlist.`;
          this.showSnackBarMessage(msg);
        }

      })
  }

  openSendInvitePopup(sender: GroupMembershipViewModel | null, recipient: UserMonikerSummaryViewModel | null) {
    if(!this.userService.userLoggedIn) {
      const msg = 'You must be logged in to perform this action.';
      this.showSnackBarMessage(msg);
      return;
    }

    const dialogRef = this.dialog.open(SendGroupInvitePopupComponent, {
      autoFocus: false,
      disableClose: true,
      data: {
        listing: sender?.listing,
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
