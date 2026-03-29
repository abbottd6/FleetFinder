import {Component, OnDestroy} from '@angular/core';
import {filter, map, shareReplay, Subject, takeUntil} from "rxjs";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {
  GroupListingFetchService
} from "../../services/api-services/group-listings-fetch-api/group-listing-fetch.service";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {AsyncPipe, DatePipe, NgIf} from "@angular/common";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatIcon} from "@angular/material/icon";
import {UserService} from "../../services/user-services/user.service";
import {
  ListingViewInteractionsService
} from "../../services/facade-services/listing-view-interactions/listing-view-interactions.service";
import {ChatHostService} from "../../services/facade-services/chat/chat-host.service";
import {MatHint} from "@angular/material/form-field";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {BreakpointObserver} from "@angular/cdk/layout";
import {UiPrefsService} from "../../services/facade-services/ui-prefs/ui-prefs.service";

@Component({
  selector: 'app-listing-details',
  standalone: true,
  templateUrl: './listing-details.component.html',
  imports: [
    AsyncPipe,
    DatePipe,
    MatIcon,
    MatMenu,
    MatMenuItem,
    NgIf,
    RouterLink,
    MatMenuTrigger,
    MatHint,
    MatProgressSpinner
  ],
  styleUrl: './listing-details.component.css'
})
export class ListingDetailsComponent implements OnDestroy {
  private destroy$: Subject<void> = new Subject<void>();
  private breakpoint = new BreakpointObserver();

  protected isLoading: boolean = true;

  private readonly groupId!: number | null;

  protected listing!: GroupListingViewModel;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private uiPrefs: UiPrefsService,
              private listingsApi: GroupListingFetchService,
              protected userService: UserService,
              protected listingInteract: ListingViewInteractionsService,
              protected chatHostSrv: ChatHostService) {

    const stringId = this.route.snapshot.paramMap.get('groupId');

    this.groupId = Number(stringId) ? Number(stringId) : null;

    if(!this.groupId) {
      this.rerouteGroupNotFound();
      return;
    }

    this.uiPrefs.loadUiPrefs();

    this.listingsApi.getGroupById(this.groupId).pipe(takeUntil(this.destroy$))
      .subscribe({
          next: (profile => {
            this.listing = profile;
            // this.checkAuth();
            this.isLoading = false;
          }),
          error: error => {
            this.rerouteGroupNotFound();
            return;
          }
        }
      )
  }

  rerouteGroupNotFound() {
    this.router.navigateByUrl('/nothing-here-page');
  }

  checkAuth() {
    this.userService.userLoggedIn$.pipe(takeUntil(this.destroy$),
      filter(val => val == true))
      .subscribe(val => {
        this.listingInteract.isLoggedIn = val;
        this.listingInteract.setSelectedListing(this.listing);
      });
  }

  userIsListingOwner(): boolean {

    const groupId = this.listing?.groupId;

    if(!this.userService.sessionUser$ || !groupId) return false;

    return this.userService.userListings.some(
      gl => gl.groupId === groupId
    );
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  isMobile$ = this.breakpoint
    .observe('(max-width: 700px)')
    .pipe(map(result => result.matches),
      shareReplay());
}
