import {DestroyRef, inject, Injectable} from '@angular/core';
import {GroupMembershipApiService} from "../../api-services/group-membership-api/group-membership-api.service";
import {UserService} from "../../user-services/user.service";
import {
  GroupMembershipViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-membership-view-model";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {BehaviorSubject, EMPTY} from "rxjs";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Page} from "../../../models/page-interface";
import {SortablePageRequest} from "../../../utils/sortable-page-request";
import {MembershipSortFields} from "../../../components/user-profile-my-groups/user-profile-my-groups.component";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmGenericComponent} from "../../../components/pop-ups/confirm-generic/confirm-generic.component";

@Injectable({
  providedIn: 'root'
})
export class GroupMembershipsInteractService {
  private destroyRef = inject(DestroyRef);

  public membershipIdx: number = 0;
  private membershipSize: number = 10;
  public membershipTotalEl!: number;
  public membershipTotalPages!: number;
  private membershipSortDir: string = 'DESC';
  public membershipSortField: MembershipSortFields = MembershipSortFields.joinedAt;

  private groupMembershipsSubject = new BehaviorSubject<GroupMembershipViewModel[]>([]);
  public groupMemberships$ = this.groupMembershipsSubject.asObservable();

  public noMemberships: boolean = true;

  constructor(private membershipsApiService: GroupMembershipApiService,
              private userService: UserService,
              private snackBar: MatSnackBar,
              private dialog: MatDialog) {}

  getMyGroupMemberships() {
    if(this.userService.userLoggedIn) {
      const pageRequest: SortablePageRequest = {
        page: this.membershipIdx,
        size: this.membershipSize,
        sortField: this.membershipSortField,
        sortDirection: this.membershipSortDir
      }
      this.membershipsApiService.getMyGroupMemberships(pageRequest).pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe((page: Page<GroupMembershipViewModel>) => {
            this.groupMembershipsSubject.next(page.content);
            this.noMemberships = page.content.length === 0;
            this.membershipIdx = page.page.number;
            this.membershipSize = page.page.size;
            this.membershipTotalEl = page.page.totalElements;
            this.membershipTotalPages = page.page.totalPages;
        });
    }
  }

  sortMembershipsPage(field: MembershipSortFields) {
    this.membershipSortField = field;
    this.getMyGroupMemberships();
  }

  getMyInviteAuthorizedMemberships() {
    if(!this.userService.userLoggedIn){
      this.snackBar.open('You must be logged in to perform this action', 'OK', {
        duration: 4000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['mobile-snackbar']
      })
      return EMPTY;
    }

    return this.membershipsApiService.getMyInviteAuthorizedMemberships();
  }

  addAcceptedInviteNewMembership(newMembership: GroupMembershipViewModel) {
    const current = this.groupMembershipsSubject.getValue();
    this.groupMembershipsSubject.next([
      newMembership,
      ...current
    ])
  }

  memberLeaveGroup(membership: GroupMembershipViewModel): void {
    const message: string = "You will be removed from this group's roster and no longer receive " +
      "notifications for this group."

    const title: string = 'Group: ' + (membership.listing.listingTitle.length > 50 ?
      membership.listing.listingTitle.substring(0, 47) + "..." : membership.listing.listingTitle);

    const dialogRef = this.dialog.open(ConfirmGenericComponent, {
      data: {
        title: title,
        message: message
      }
    })

    dialogRef.afterClosed().pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(result => {
        if(result) {
          this.membershipsApiService.memberLeaveGroup(membership.listing.groupId).pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
              next: () => {
                const before = this.groupMembershipsSubject.getValue();
                const idx = before.findIndex(m => m.listing.groupId === membership.listing.groupId);
                this.groupMembershipsSubject.next([
                  ...before.slice(0, idx),
                  ...before.slice(idx + 1)
                ])
              }
            })
        } else {
          return;
        }
      })
  }
}
