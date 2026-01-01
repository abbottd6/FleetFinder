import {
  afterNextRender,
  AfterViewInit,
  Component, ElementRef,
  EventEmitter,
  inject,
  OnDestroy,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
import {GroupListingFetchService} from "../../services/api-services/group-listings-fetch-api/group-listing-fetch.service";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {environment} from "../../../environments/environment";
import {TooltipPosition} from "@angular/material/tooltip";
import {MatSort, Sort, SortDirection} from "@angular/material/sort";
import {MatTableDataSource} from "@angular/material/table";
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {BreakpointObserver} from "@angular/cdk/layout";
import { map, Observable, shareReplay, Subject, takeUntil } from "rxjs";
import {
  FilterService,
  ListingFilterState
} from "../../services/api-services/filter-api/filter.service";
import {ListingFilterRequest} from "../../models/listing-filter/listing-filter-request";
import {AuthService} from "../../services/auth/auth-services/auth.service";
import {MatDialog} from "@angular/material/dialog";
import {LayoutMode} from "../input-fields/search-bar/search-bar.component";
import {UserService} from "../../services/user-services/user.service";
import {
  ListingViewInteractionsService
} from "../../services/facade-services/listing-view-interactions/listing-view-interactions.service";
import {UiPrefsService} from "../../services/facade-services/ui-prefs/ui-prefs.service";
import {MatMenuTrigger} from "@angular/material/menu";
import {QuickAccessMenuService} from "../../services/component-services/quick-access-menu/quick-access-menu.service";
import {
  ListingOwnerActionsService
} from "../../services/facade-services/listing-view-interactions/listing-owner-actions.service";

@Component({
    selector: 'app-group-listings-table',
    templateUrl: './group-listings.component.html',
    styleUrl: './group-listings.component.css',
    standalone: false
})

export class GroupListingsComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  @ViewChild(MatMenuTrigger) menuTrigger!: MatMenuTrigger;
  @ViewChild('contextMenuAnchor', { read: ElementRef })
  protected contextMenuAnchor!: ElementRef<HTMLElement>;


  @Output() filtersUpToDate = new EventEmitter<boolean>();

  private destroy$ = new Subject<void>();
  private breakpointObserver = inject(BreakpointObserver);
  private _liveAnnouncer = inject(LiveAnnouncer);
  readonly dialog = inject(MatDialog);

  positionOptions: TooltipPosition[] = ['after', 'before', 'above', 'below', 'left', 'right'];

  pageIndex = 0;
  pageSize = 25;
  totalElements = 0;
  submittedState: ListingFilterState | null = null;
  sortActive = 'creationTimestamp';
  sortDirection: SortDirection = 'desc';

  displayedColumns: string[] = ['indicators', 'title', 'status', 'category', 'pvp', 'system', 'roles', 'group-size', 'updated'];
  mobileColumns: string[] = ['indicators', 'details']
  dataSource = new MatTableDataSource<GroupListingViewModel>();
  noResults!: boolean;

  constructor(private groupListingService: GroupListingFetchService,
              private filter: FilterService,
              protected listingInteract: ListingViewInteractionsService,
              private uiPrefService: UiPrefsService,
              protected quickMenu: QuickAccessMenuService,
              private auth: AuthService,
              private userService: UserService,
              protected ownerService: ListingOwnerActionsService) {

    this.listingInteract.refresh$.pipe(takeUntil(this.destroy$)).subscribe( reason => {
      if(reason === 'hide' || reason === 'report' || reason === 'delete') {
        this.reloadListings();
      }
    })

    afterNextRender(() => {
      this.uiPrefService.clickedCleanupCheck();
    })
  }

  ngOnInit(): void {
    this.uiPrefService.uiPrefs = this.uiPrefService.loadUiPrefs();

    this.filter.pushStoredState(this.uiPrefService.uiPrefs.storedFilters, this.filter.pullState());

    if(this.auth.isLoggedIn$) {
      this.userService.refreshUser();
    }

    this.applyFiltersFromChild(this.filter.pullState());

    this.auth.isLoggedIn$.pipe(takeUntil(this.destroy$)).subscribe(
      val => this.listingInteract.isLoggedIn = val);
  }

  ngAfterViewInit() {
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
    this.uiPrefService.displayQuickAccessMenuHint();

    this.quickMenu.registerMenu(this.menuTrigger, this.contextMenuAnchor)
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
    this.uiPrefService.uiPrefs.storedFilters = this.filter.toPersistedState(state);
    this.uiPrefService.saveUiPrefs(this.uiPrefService.uiPrefs);
  }

  announceSortChange(sortState: Sort) {
    if (sortState.direction) {
      this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
    } else {
      this._liveAnnouncer.announce('Sorting cleared');
    }
  }

  loadGroupListings(dto: ListingFilterRequest, idx: number, sz: number, sortA: string, sortD: string) {
    this.groupListingService.searchGroupListings(dto, idx, sz, sortA, sortD).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (page) => {
          if (!environment.production) {
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
          this.noResults = (this.dataSource.data.length === 0);
        }
      });
  }

  isReported(id: number, reportIds: Set<number> | null): boolean {
    if(reportIds == undefined) {
      return false;
    }
    return !!reportIds && reportIds.has(id);
  }

  layoutMode$: Observable<LayoutMode> = this.breakpointObserver
    .observe([
      '(max-width: 900px)',
      '(min-width: 901px) and (max-width: 1650px)',
      '(min-width: 1051px)'
    ])
    .pipe(
      map(state => {
        if (state.breakpoints['(max-width: 900px)']) {
          return 'handheld';
        }
        if (state.breakpoints['(min-width: 901px) and (max-width: 1650px)']) {
          return 'mobile';
        }

        return 'full';
      }),
      shareReplay(1)
    );

  /* ------------------------------------ INTERFACE TO UI PREFS SERVICE ----------------------------------------------*/

  isRowClicked(row: GroupListingViewModel): boolean {
    return this.uiPrefService.uiPrefs.clickedRowIds.has(row.groupId);
  }

}
