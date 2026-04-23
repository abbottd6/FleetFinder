import {DestroyRef, inject, Injectable} from '@angular/core';
import {GroupMembershipApiService} from "../../api-services/group-membership-api/group-membership-api.service";
import {UserService} from "../../user-services/user.service";
import {
  GroupMembershipViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-membership-view-model";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {BehaviorSubject, EMPTY, Observable, ReplaySubject} from "rxjs";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Page} from "../../../models/page-interface";

@Injectable({
  providedIn: 'root'
})
export class GroupMembershipsInteractService {
  private destroyRef = inject(DestroyRef);

  private groupMembershipsSubject = new BehaviorSubject<GroupMembershipViewModel[]>([]);
  public groupMemberships$ = this.groupMembershipsSubject.asObservable();

  public noMemberships: boolean = true;

  constructor(private membershipsApiService: GroupMembershipApiService,
              private userService: UserService,
              private snackBar: MatSnackBar) {}

  getMyGroupMemberships() {
    if(this.userService.userLoggedIn) {
      this.membershipsApiService.getMyGroupMemberships().pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe((page: Page<GroupMembershipViewModel>) => {
            this.groupMembershipsSubject.next(page.content);
            this.noMemberships = page.content.length === 0;
        });
    }
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

}
