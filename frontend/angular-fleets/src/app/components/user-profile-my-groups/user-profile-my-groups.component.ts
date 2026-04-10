import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {
  GroupMembershipViewModel
} from "../../models/group-management-models/view-models/group-membership/group-membership-view-model";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {
  GroupMembershipsInteractService
} from "../../services/facade-services/group-management/group-memberships-interact.service";
import {Subject} from "rxjs";
import {GroupsChipComponent} from "./groups-chip/groups-chip.component";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {UiPrefsService} from "../../services/facade-services/ui-prefs/ui-prefs.service";

@Component({
  selector: 'app-user-profile-my-groups',
  standalone: true,
  templateUrl: './user-profile-my-groups.component.html',
  imports: [
    NgIf,
    AsyncPipe,
    NgForOf,
    GroupsChipComponent
  ],
  styleUrl: './user-profile-my-groups.component.css'
})
export class UserProfileMyGroupsComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>;

  @Output() emitListing = new EventEmitter<GroupListingViewModel>();
  constructor(protected groupMemberService: GroupMembershipsInteractService,
              private uiPrefs: UiPrefsService) {}

  ngOnInit() {
    this.groupMemberService.getMyGroupMemberships();
    this.uiPrefs.loadUiPrefs();
  }

  passEmissionToParent(listing: GroupListingViewModel) {
    this.emitListing.emit(listing);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
