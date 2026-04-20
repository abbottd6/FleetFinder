import {DestroyRef, inject, Injectable} from '@angular/core';
import {GroupMembershipApiService} from "../../api-services/group-membership-api/group-membership-api.service";
import {UserService} from "../../user-services/user.service";
import {
  GroupMembershipViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-membership-view-model";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {Page} from "../../api-services/group-listings-fetch-api/group-listing-fetch.service";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class GroupMembershipsInteractService {
  private destroyRef = inject(DestroyRef);

  private groupMembershipsSubject = new BehaviorSubject<GroupMembershipViewModel[]>([]);
  public groupMemberships$ = this.groupMembershipsSubject.asObservable();

  public noMemberships: boolean = true;

  constructor(private membershipsApiService: GroupMembershipApiService, private userService: UserService) {}

  getMyGroupMemberships() {
    if(this.userService.userLoggedIn) {
      this.membershipsApiService.getMyGroupMemberships().pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe((page: Page<GroupMembershipViewModel>) => {
            this.groupMembershipsSubject.next(page.content);
            this.noMemberships = page.content.length === 0;
        });
    }
  }

}
