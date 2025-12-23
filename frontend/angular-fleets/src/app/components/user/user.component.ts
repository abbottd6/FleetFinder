import {
  Component,
  inject, OnDestroy,
  OnInit, ViewChild,
} from '@angular/core';
import {AuthService} from "../../services/auth/auth-services/auth.service";
import {map, Observable, shareReplay, Subject, takeUntil} from "rxjs";
import {PrivateUser} from "../../models/private-user/private-user";
import {RouterModule} from "@angular/router";
import {CommonModule} from "@angular/common";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatListItem, MatNavList} from "@angular/material/list";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import { BreakpointObserver } from "@angular/cdk/layout";
import {UserAcctListingsTableComponent} from "../user-acct-listings-table/user-acct-listings-table.component";
import {MatButtonModule} from "@angular/material/button";
import {UserService} from "../../services/user-services/user.service";
import {CloseValue, GroupListingModalComponent} from "../group-listing-modal/group-listing-modal.component";
import {environment} from "../../../environments/environment";
import {ModListingsTableComponent} from "../mod-tools/mod-listings-table/mod-listings-table.component";
import {UserProfileBookmarksComponent} from "../user-profile-bookmarks/user-profile-bookmarks.component";
import {
  ListingViewInteractionsService
} from "../../services/facade-services/listing-view-interactions/listing-view-interactions.service";
import {ModParentPanelComponent} from "../mod-tools/mod-parent-panel/mod-parent-panel.component";

@Component({
    selector: 'app-user',
    templateUrl: './user.component.html',
    styleUrls: [
      './user.component.css',
    ],
  imports: [CommonModule, RouterModule, MatSidenavModule, MatNavList, MatListItem,
    UserAcctListingsTableComponent, MatButtonModule, GroupListingModalComponent,
    UserProfileBookmarksComponent, ModParentPanelComponent],
    standalone: true
})
export class UserComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  private breakpointObserver = inject(BreakpointObserver);

  @ViewChild('bookmarks') bookmarks!: UserProfileBookmarksComponent;

  //modal popup vars
  selectedListing: GroupListingViewModel | null = null;

  groupListings: GroupListingViewModel[] = []
  localUser$: Observable<PrivateUser>;
  selectedTab: 'listings'|'bookmarks'|'templates'|'profile'|'content_mod' = 'listings';
  shouldDisplayMod$: boolean = false;

  constructor(public userService: UserService, protected auth: AuthService,
              protected listingInteract: ListingViewInteractionsService) {
    this.localUser$ = this.userService.localUser$;

    this.localUser$.pipe(
      map(user => user.groupListingsDto ?? []),
      takeUntil(this.destroy$)
    )
      .subscribe(listings => this.groupListings = listings);
  }

  ngOnInit() {
    this.userService.refreshUser();
    this.shouldDisplayMod$ = this.askShouldDisplayMod();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  askShouldDisplayMod(): boolean {
    console.log("Role: ", this.userService.getRole())
    return this.userService.getRole() == 'mod';
  }

  selectTab(tab: typeof this.selectedTab){
    this.selectedTab = tab;
  }

  onListingSelected(listing: GroupListingViewModel) {
    this.selectedListing = listing;
    this.listingInteract.isModalVisible = true;
    console.log("Role:", this.userService.getRole());
    if(!environment.production) {
      console.log("Parent modal visibility: ", this.listingInteract.isModalVisible);
    }
  }

  isMobile$ = this.breakpointObserver
    .observe('(max-width: 1200px)')
    .pipe(map(result => result.matches),
      shareReplay());
}
