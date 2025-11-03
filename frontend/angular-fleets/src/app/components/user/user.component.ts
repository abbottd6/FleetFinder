import {
  Component,
  ViewChild,
  inject,
  OnInit,
  AfterViewInit,
  Inject,
  OnChanges,
  SimpleChanges,
  Input
} from '@angular/core';
import {AuthService} from "../../services/auth/auth-services/auth.service";
import {map, Observable, shareReplay} from "rxjs";
import {PrivateUser} from "../../models/private-user/private-user";
import {Router, RouterModule} from "@angular/router";
import {CommonModule} from "@angular/common";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatListItem, MatNavList} from "@angular/material/list";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import { BreakpointObserver } from "@angular/cdk/layout";
import {UserAcctListingsTableComponent} from "../user-acct-listings-table/user-acct-listings-table.component";
import {MatButton, MatButtonModule, MatIconButton} from "@angular/material/button";
import {UserService} from "../../services/user-services/user.service";
import {SelectionModel} from "@angular/cdk/collections";

@Component({
    selector: 'app-user',
    templateUrl: './user.component.html',
    styleUrls: [
      './user.component.css',
      '../create-listing/create-listing.component.css',
    ],
  imports: [CommonModule, RouterModule, MatSidenavModule, MatNavList, MatListItem,
    UserAcctListingsTableComponent, MatButtonModule],
    standalone: true
})
export class UserComponent implements OnInit {
  private breakpointObserver = inject(BreakpointObserver);

  groupListings: GroupListingViewModel[] = []

  localUser$: Observable<PrivateUser>;

  selectedTab: 'profile'|'listings'|'saved'|'edit' = 'profile';

  selectTab(tab: typeof this.selectedTab){
    this.selectedTab = tab;
  }

  ngOnInit() {
    this.userService.refreshUser()
  }

  constructor(public userService: UserService, protected auth: AuthService) {
    this.localUser$ = this.userService.localUser$;

    this.localUser$.pipe(
      map(user => user.groupListingsDto ?? [])
    )
      .subscribe(listings => this.groupListings = listings);
  }

  isMobile$ = this.breakpointObserver
    .observe('(max-width: 1200px)')
    .pipe(map(result => result.matches),
      shareReplay());
}
