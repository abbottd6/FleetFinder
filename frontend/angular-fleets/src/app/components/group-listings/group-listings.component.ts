import {
  afterNextRender,
  AfterViewInit,
  Component,
  EventEmitter,
  inject,
  OnDestroy,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
import {GroupListingFetchService} from "../../services/group-listing-services/group-listing-fetch.service";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {environment} from "../../../environments/environment";
import {MatSnackBar} from "@angular/material/snack-bar";
import {TooltipPosition} from "@angular/material/tooltip";
import {MatSort, Sort, SortDirection} from "@angular/material/sort";
import {MatTableDataSource} from "@angular/material/table";
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {BreakpointObserver} from "@angular/cdk/layout";
import {combineLatest, map, Observable, of, shareReplay, Subject, take, takeUntil,} from "rxjs";
import {
  FilterService,
  ListingFilterState,
  PersistedFilterState
} from "../../services/api-lookup-services/filter.service";
import {ListingFilterRequest} from "../../models/listing-filter/listing-filter-request";
import {AddBookmarkRequest} from "../../models/bookmark-requests/add-bookmark-request";
import {UserBookmarkService} from "../../services/user-services/user-bookmark.service";
import {AuthService} from "../../services/auth/auth-services/auth.service";
import {ListingReportService} from "../../services/listing-report-services/listing-report.service";
import {SubmitListingReport} from "../../models/report-requests/submit-listing-report";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmReportComponent} from "../pop-ups/confirm-report/confirm-report.component";
import {HideListingRequest} from "../../models/listing-filter/hide-listing-request.model";
import {HiddenListingsService} from "../../services/user-services/hidden-listings.service";
import {LayoutMode} from "../input-fields/search-bar/search-bar.component";
import {DontShowMeAgainPopup} from "../pop-ups/dont-show-me-again-popup/dont-show-me-again-popup";
import {CloseValue} from "../group-listing-modal/group-listing-modal.component";
import {UiCleanupService} from "../../services/cleanup-services/ui-cleanup.service";
import {UserService} from "../../services/user-services/user.service";

export const UI_PREFS_KEY = 'ff_ui_prefs';
export const MAX_CLICKED = 300;
export const CLICKED_EVICT_COUNT = 1;
export const ONE_DAY_MS = 24 * 60 * 60 * 1000;
export const SOFT_MAX_CLICKED = 3;

export interface UiPrefs {
  clickedRowIds: Set<number>;
  lastClickedClean: number;
  hideHiddenListingHint: boolean;
  hideReportedListingHint: boolean;
  hideBookmarkedListingHint: boolean;
  storedFilters: PersistedFilterState;
}

@Component({
    selector: 'app-group-listings-table',
    templateUrl: './group-listings.component.html',
    styleUrl: './group-listings.component.css',
    standalone: false
})

export class GroupListingsComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  @Output() filtersUpToDate = new EventEmitter<boolean>();

  private destroy$ = new Subject<void>();
  private breakpointObserver = inject(BreakpointObserver);
  private _liveAnnouncer = inject(LiveAnnouncer)
  private bmService = inject(UserBookmarkService);
  private reportService = inject(ListingReportService);
  readonly dialog = inject(MatDialog);


  positionOptions: TooltipPosition[] = ['after', 'before', 'above', 'below', 'left', 'right'];
  selectedListing: GroupListingViewModel | null = null;
  isModalVisible: boolean = false;

  uiPrefs!: UiPrefs;

  pageIndex = 0;
  pageSize = 25;
  totalElements = 0;
  submittedState: ListingFilterState | null = null;
  sortActive = 'creationTimestamp';
  sortDirection: SortDirection = 'desc';

  displayedColumns: string[] = ['options', 'title', 'status', 'category', 'pvp', 'system', 'roles', 'updated'];
  mobileColumns: string[] = ['options', 'details']
  dataSource = new MatTableDataSource<GroupListingViewModel>();

  constructor(private groupListingService: GroupListingFetchService, private snackBar: MatSnackBar,
              private filter: FilterService, private auth: AuthService, private hideService: HiddenListingsService,
              private uiCleanup: UiCleanupService, private userService: UserService) {

    afterNextRender(() => {
      this.clickedCleanupCheck();
    })
  }

  bookmarkedIds$!: Observable<Set<number>>;
  selectedIsBookmarked$!: Observable<boolean>;
  isLoggedIn!: boolean;

  ngOnInit(): void {
    this.uiPrefs = this.loadUiPrefs();

    this.filter.pushStoredState(this.uiPrefs.storedFilters, this.filter.pullState());


    if(this.auth.isLoggedIn$) {
      this.userService.refreshUser();
    }

    this.applyFiltersFromChild(this.filter.pullState());

    this.auth.isLoggedIn$.pipe(takeUntil(this.destroy$)).subscribe(
      val => this.isLoggedIn = val);

    this.bmService.getBookmarksBrief();
  }

  ngAfterViewInit() {
    this.bookmarkedIds$ = this.bmService.bookmarksBrief$.pipe(
      map((gIds: number[]) => new Set<number>(gIds))
    );

    this.paginator.page.pipe(takeUntil(this.destroy$))
      .subscribe((event: PageEvent) => {
      this.pageIndex = event.pageIndex;
      this.pageSize = event.pageSize;
      this.reloadListings();
    })

    this.sort.sortChange.pipe(takeUntil(this.destroy$))
      .subscribe((event: Sort) => {
      this.sortActive = event.active;
      this.sortDirection = event.direction || 'desc';

      this.pageIndex = 0;
      if(this.paginator) {
        this.paginator.pageIndex = 0;
      }

      this.reloadListings();
    })

    this.dataSource.sort = this.sort;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private reloadListings(): void {
    const state = this.submittedState ?? this.filter.pullState();
    const filterDto = new ListingFilterRequest(state);
    this.submittedState = structuredClone(state);

    this.loadGroupListings(filterDto, this.pageIndex, this.pageSize, this.sortActive, this.sortDirection);
  }

  applyFiltersFromChild(state: ListingFilterState): void {
    this.submittedState = state;

    this.pageIndex = 0;

    if(this.paginator) {
      this.paginator.pageIndex = 0;
    }

    this.reloadListings();
    // state.searchInput = null;
    this.uiPrefs.storedFilters = this.filter.toPersistedState(state);
    this.saveUiPrefs(this.uiPrefs);
  }

  isRowClicked(row: GroupListingViewModel): boolean {
    return this.uiPrefs.clickedRowIds.has(row.groupId);
  }

  announceSortChange(sortState: Sort) {
    if (sortState.direction) {
      this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
    } else {
      this._liveAnnouncer.announce('Sorting cleared');
    }
  }

  loadGroupListings(dto: ListingFilterRequest, idx: number, sz: number, sortA: string, sortD: string) {
    this.groupListingService.searchGroupListings(dto, idx, sz, sortA, sortD)
      .subscribe({
        next: (page) => {
          if(!environment.production) {
            console.log('Data received in component:', page);
          }
          this.dataSource.data = page.content;
          this.totalElements = page.totalElements;
          this.pageSize = page.size;
          this.pageIndex = page.number;
        },
        error: (error) => {
          console.error('Error fetching group listings from component:', error);
        },
        complete: () => {
          if(!environment.production) {
            console.log('Group listings fetching completed.');
          }
        }
    });
  }

  //on-row-click instructions for groupListing modal popup
  onRowClick(tempListing: GroupListingViewModel) {
    this.selectedListing = tempListing;
    this.selectedIsBookmarked$ = combineLatest([
      this.bookmarkedIds$,
      of(this.selectedListing.groupId),
    ]).pipe(
      map(([ids, selectedId]) => !!selectedId && ids.has(selectedId))
    );
    this.isModalVisible = true;
    // console.log("CLICKED ROWS: ", this.clickedRows)
  }

  saveRowClick(row: number) {
    if (this.uiPrefs.clickedRowIds.size >= MAX_CLICKED) {
      let removed = 0;
      for(const oldest of Array.from(this.uiPrefs.clickedRowIds)) {
        this.uiPrefs.clickedRowIds.delete(oldest);
        removed++;
        if(removed >= CLICKED_EVICT_COUNT) break;
      }
    }

    if(this.uiPrefs.clickedRowIds.has(row)) {
      this.uiPrefs.clickedRowIds.delete(row);
    }

    this.uiPrefs.clickedRowIds.add(row);

    this.saveUiPrefs(this.uiPrefs);
  }

  private loadUiPrefs() {
    try {
      const localPrefs = localStorage.getItem(UI_PREFS_KEY);
      if(!localPrefs) {
        return {
          clickedRowIds: new Set<number>(),
          lastClickedClean: 0,
          hideHiddenListingHint: false,
          hideReportedListingHint: false,
          hideBookmarkedListingHint: false,
          storedFilters: this.filter.pullState()
        };
      }
      const parsed = JSON.parse(localPrefs) as Partial<UiPrefs>
      return {
        clickedRowIds: new Set<number>(parsed.clickedRowIds ?? []),
        lastClickedClean: parsed.lastClickedClean ?? 0,
        hideHiddenListingHint: parsed.hideHiddenListingHint ?? false,
        hideReportedListingHint: parsed.hideReportedListingHint ?? false,
        hideBookmarkedListingHint: parsed.hideBookmarkedListingHint ?? false,
        storedFilters: parsed.storedFilters ?? this.filter.pullState()
      };

    } catch {
      localStorage.removeItem(UI_PREFS_KEY);
      return {
        clickedRowIds: new Set<number>([]),
        lastClickedClean: 0,
        hideHiddenListingHint: false,
        hideReportedListingHint: false,
        hideBookmarkedListingHint: false,
        storedFilters: this.filter.pullState()
      }
    }
  }

  clickedCleanupCheck() {
    const lastClean = this.uiPrefs.lastClickedClean;

    if((Date.now() - lastClean >= ONE_DAY_MS) || (this.uiPrefs.clickedRowIds.size >= SOFT_MAX_CLICKED)) {
      this.uiCleanup.cleanClickedListings(Array.from(this.uiPrefs.clickedRowIds)).pipe(takeUntil(this.destroy$)).subscribe({
        next: (response: {cleaned: number[] }) => {
          this.uiPrefs.clickedRowIds = new Set(response.cleaned);
          this.uiPrefs.lastClickedClean = Date.now();
          this.saveUiPrefs(this.uiPrefs);
        }
      })
    }
  }

  private saveUiPrefs(prefs: UiPrefs): void {
    localStorage.setItem(UI_PREFS_KEY, JSON.stringify({
      ...prefs,
      clickedRowIds: Array.from(prefs.clickedRowIds),
    }));
  }

  addBookmark(listingId: number) {
    if(!this.isLoggedIn) {
      this.snackBar.open("You must log in to access bookmarks.", 'OK', {
        duration: 5000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['mobile-snackbar']})
      return;
    }
    const request = new AddBookmarkRequest(listingId);
    this.bmService.addBookmark(request).pipe(takeUntil(this.destroy$)).subscribe( {
      next: (response: { listingTitle: string; }) => {
        this.snackBar.open(`"${response.listingTitle}" added to bookmarks.`, 'OK', {
          duration: 4000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['mobile-snackbar']
        });

        if (!this.uiPrefs.hideBookmarkedListingHint) {
          const dialogRef = this.dialog.open(DontShowMeAgainPopup, {
            data: {
              message: "<p>This listing has been added to your bookmarks.</p>" +
                "<p>Bookmarks can be accessed by visiting your 'Profile' " +
                "page and viewing the 'Bookmarks' tab.</p>"
            }
          });

          dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(dontShow => {
            if (dontShow) {
              this.uiPrefs.hideBookmarkedListingHint = true;
              this.saveUiPrefs(this.uiPrefs);
            }
          })
        }
      },
      error: (err) => {
        console.error(err);
      }
    })
  }

  userHideListing(listingId: number) {
    if(!this.isLoggedIn) {
      this.snackBar.open("You must log in to hide listings.", 'OK', {
        duration: 5000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['mobile-snackbar']})
      return;
    }
    const request = new HideListingRequest(listingId);
    this.hideService.addHidden(request).pipe(takeUntil(this.destroy$)).subscribe({
      next: (response: { Response: string; }) => {
        this.snackBar.open(`${response.Response}`, 'OK', {
          duration: 4000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['mobile-snackbar']
        });
        if(!this.uiPrefs.hideHiddenListingHint) {
          const dialogRef = this.dialog.open(DontShowMeAgainPopup, {
            data: {
              message: "<p>This listing has been hidden and will no longer appear in your search results.</p>" +
                "<p>To unhide listings, use the 'Hidden' menu to the right above the listings table.</p>"
            }
          });

          dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(dontShow => {
            if (dontShow) {
              this.uiPrefs.hideHiddenListingHint = true;
              this.saveUiPrefs(this.uiPrefs);
            }
          })
        }
      },

      error: (err) => {
        console.error(err);
      },

      complete: () => {
          this.reloadListings();
      }
    })
  }

  deleteBookmark(listingId: number) {
    const request = listingId;
    if (!environment.production) {
      console.log(request);
    }
    this.bmService.deleteBookmark(request).pipe(takeUntil(this.destroy$)).subscribe( {
        next: (response: { message: string; }) =>
          this.snackBar.open(`${response.message}`, 'OK', {
            duration: 3000,
            verticalPosition: 'top',
            horizontalPosition: 'center',
            panelClass: ['mobile-snackbar']})
      }
    )
  }

  isBookmarked(id: number, bookmarkIds: Set<number> | null): boolean {
    if(bookmarkIds == undefined) {
      return false;
    }
    return !!bookmarkIds && bookmarkIds.has(id);
  }

  openConfirmReport(listing: GroupListingViewModel): void {
    if(!this.isLoggedIn) {
      this.snackBar.open("You must log in to submit reports.", 'OK', {
        duration: 5000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['mobile-snackbar']})
      return;
    }

    this.reportService.reportOptions$
      .pipe(take(1))
      .subscribe(options => {
        const dialogRef = this.dialog.open(ConfirmReportComponent, {
          data: {
            listing,
            options
          }
        });

        dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(selected => {
          if (selected) {
            this.submitReport(listing.groupId, selected);
          }
        });
    });
  }

  submitReport(listingId: number, basisId: number) {
    const lr = new SubmitListingReport(listingId, basisId)

    this.reportService.submitReport(lr).pipe(takeUntil(this.destroy$)).subscribe({
      next: (response: { reportId: string; }) => {
        this.snackBar.open(`Report submitted. Thank you.`, 'OK', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['mobile-snackbar']
        });
        if(!this.uiPrefs.hideReportedListingHint) {
          const dialogRef = this.dialog.open(DontShowMeAgainPopup, {
            data: {
              message: "<p>Listing Reported.</p>" +
                "<p>Reported listings will no longer appear in your search results. This action cannot be undone.</p>" +
                "<p>If you just want to hide a particular listing, use the 'hide' feature instead. Hide actions can " +
                "be undone. </p>"
            }
          });

          dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(dontShow => {
            if (dontShow) {
              this.uiPrefs.hideReportedListingHint = true;
              this.saveUiPrefs(this.uiPrefs);
            }
          })
        }
      },

      error: (err) => {
        console.error(err);
      },

      complete: () => {
        this.reloadListings();
      }
    });
  }

  isReported(id: number, reportIds: Set<number> | null): boolean {
    if(reportIds == undefined) {
      return false;
    }
    return !!reportIds && reportIds.has(id);
  }

  //on close instructions for groupListing modal popup
  onModalClose(action: CloseValue) {
    if(!environment.production) {
      console.log("Modal closed");
    }
    this.isModalVisible = false;
    this.selectedListing = null;

    if(!action.value) return;

    if(action.value && action.group) {
      switch (action.value) {
        case 'hide':
          return this.userHideListing(action.group.groupId);
        case 'bookmark':
          return this.addBookmark(action.group.groupId);
        case 'unbookmark':
          return this.deleteBookmark(action.group.groupId);
        case 'report':
          return this.openConfirmReport(action.group)
      }
    }
  }

  layoutMode$: Observable<LayoutMode> = this.breakpointObserver
    .observe([
      '(max-width: 900px)',
      '(min-width: 901px) and (max-width: 1375px)',
      '(min-width: 1051px)'
    ])
    .pipe(
      map(state => {
        if (state.breakpoints['(max-width: 900px)']) {
          return 'handheld';
        }
        if (state.breakpoints['(min-width: 901px) and (max-width: 1375px)']) {
          return 'mobile';
        }

        return 'full';
      }),
      shareReplay(1)
    );
}
