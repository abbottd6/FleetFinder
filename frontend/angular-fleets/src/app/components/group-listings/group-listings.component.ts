import {
  AfterViewInit,
  booleanAttribute,
  Component,
  EventEmitter, Inject,
  inject,
  Input, OnDestroy,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
import {GroupListingFetchService, Page} from "../../services/group-listing-services/group-listing-fetch.service";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {environment} from "../../../environments/environment";
import {MatSnackBar} from "@angular/material/snack-bar";
import {TooltipPosition} from "@angular/material/tooltip";
import {MatSort, MatSortHeader, Sort, SortDirection} from "@angular/material/sort";
import {MatTableDataSource} from "@angular/material/table";
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {BreakpointObserver} from "@angular/cdk/layout";
import {BehaviorSubject, map, shareReplay, Subject, takeUntil} from "rxjs";
import {FilterService, ListingFilterState} from "../../services/api-lookup-services/filter.service";
import {ListingFilterRequest} from "../../models/listing-filter/listing-filter-request";
import {UserListingService} from "../../services/group-listing-services/user-listing.service";
import {AddBookmarkRequest} from "../../models/bookmark-requests/add-bookmark-request";

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
  private CLICKED_KEY = 'ff_user_clicked_listings';
  private _liveAnnouncer = inject(LiveAnnouncer)

  positionOptions: TooltipPosition[] = ['after', 'before', 'above', 'below', 'left', 'right'];
  selectedListing: GroupListingViewModel | null = null;
  isModalVisible: boolean = false;
  clickedRows = new Set<number>();

  pageIndex = 0;
  pageSize = 25;
  totalElements = 0;
  submittedState: ListingFilterState | null = null;
  sortActive = 'creationTimestamp';
  sortDirection: SortDirection = 'desc';

  /* TO DO: set up bookmarks and change this */
  userBookmarks: GroupListingViewModel[] = [];

  displayedColumns = ['options', 'title', 'status', 'category', 'pvp', 'system', 'roles', 'updated'];
  dataSource = new MatTableDataSource<GroupListingViewModel>();

  constructor(private groupListingService: GroupListingFetchService, private snackBar: MatSnackBar,
              private filter: FilterService, private userListingService: UserListingService) {}

  ngOnInit(): void {
    this.applyFiltersFromChild(this.filter.pullState())
  }

  ngAfterViewInit() {
    //just for page styling to show clicked listings
    this.loadClickedListings();


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
  }



  isRowClicked(row: GroupListingViewModel): boolean {
    return this.clickedRows.has(row.groupId);
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
    // if(!environment.production) {
    //   console.log("HERE IS THE LISTING DATA: ", tempListing);
    // }
    // if(!environment.production) {
    //   console.log("Logging selected listing ID: ", this.selectedListing.groupId);
    // }
    this.isModalVisible = true;
    // if(!environment.production) {
    //   console.log("Parent modal visibility: ", this.isModalVisible);
    // }
    // console.log("CLICKED ROWS: ", this.clickedRows)
  }

  saveRowClick(row: number) {
    this.clickedRows.add(row);
    const arr = Array.from(this.clickedRows);
    localStorage.setItem(this.CLICKED_KEY, JSON.stringify(arr));
    // console.log("clickedRows saved: ", this.clickedRows)
  }

  private loadClickedListings() {
    const clickedListings = localStorage.getItem(this.CLICKED_KEY);
    if (!clickedListings) return;

    try {
      const arr: number[] = JSON.parse(clickedListings);
      this.clickedRows = new Set(arr);
    } catch {
      //
    }
  }

  //on close instructions for groupListing modal popup
  onModalClose() {
    if(!environment.production) {
      console.log("Modal closed");
    }
    this.isModalVisible = false;
    this.selectedListing = null;
  }

  addBookmark(listingId: number) {
    const request = new AddBookmarkRequest(listingId);
    console.log(request);

    this.userListingService.addBookmark(request).subscribe({
        next: response => {
          if(!environment.production) {
            console.log(response)
          }
          alert(`${response.listingTitle} added to bookmarks.`);
        },
        error: err => {
          alert(`There was an error creating your listing: ${err.message}`);
        }
      }
    )
  }

  isMobile$ = this.breakpointObserver
    .observe('(max-width: 1350px)')
    .pipe(map(result => result.matches),
      shareReplay());
}
