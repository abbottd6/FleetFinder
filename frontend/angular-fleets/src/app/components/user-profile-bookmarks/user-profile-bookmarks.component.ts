import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  inject,
  OnDestroy,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
import {
  MatTableDataSource
} from "@angular/material/table";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {AsyncPipe, NgIf} from "@angular/common";
import {BookmarkApiService} from "../../services/api-services/bookmarks-api/bookmark-api.service";
import {map, Observable, shareReplay, Subject, takeUntil} from "rxjs";
import {BreakpointObserver} from "@angular/cdk/layout";
import {SelectionModel} from "@angular/cdk/collections";
import {
  ListingViewInteractionsService
} from "../../services/facade-services/listing-view-interactions/listing-view-interactions.service";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {LayoutMode} from "../input-fields/search-bar/search-bar.component";
import {
  DesktopTableViewComponent
} from "../listing-tables/desktop-table-view/desktop-table-view/desktop-table-view.component";
import {MobileFeedViewComponent} from "../listing-tables/mobile-feed-view/mobile-feed-view.component";

@Component({
  selector: 'app-user-bookmarks-table',
  standalone: true,
  templateUrl: './user-profile-bookmarks.component.html',
  imports: [
    NgIf,
    AsyncPipe,
    MatPaginator,
    MobileFeedViewComponent,
    DesktopTableViewComponent
  ],
  styleUrl: './user-profile-bookmarks.component.css'
})

export class UserProfileBookmarksComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  private destroy$: Subject<void> = new Subject<void>();
  private breakpointObserver = inject(BreakpointObserver);

  @Output() listingForModal = new EventEmitter<GroupListingViewModel>();

  fullColumns = [ 'select', 'title', 'status', 'category', 'pvp', 'system', 'roles', 'updated' ]
  mobileColumns = ['select', 'details']
  dataSource = new MatTableDataSource<GroupListingViewModel>();
  selection = new SelectionModel<GroupListingViewModel>(true, [])
  noResults: boolean = true;

  pageIndex = 0;
  pageSize = 10;
  totalElements = 0;

  constructor(private userBms: BookmarkApiService,
              protected listingInteract: ListingViewInteractionsService) {

    this.loadBookmarks(this.pageIndex, this.pageSize);
  }

  ngOnInit() {
    this.listingInteract.refresh$.pipe(takeUntil(this.destroy$)).subscribe(reason => {
      if(!(reason === 'bookmark')) {
        this.loadBookmarks(this.pageIndex = 0, this.pageSize);
        this.selection.clear();
      }
    })
  }

  ngAfterViewInit() {
    this.paginator.page.pipe(takeUntil(this.destroy$))
      .subscribe((event: PageEvent) => {
        this.pageIndex = event.pageIndex;
        this.pageSize = event.pageSize;
        this.loadBookmarks(this.pageIndex, this.pageSize);
      })
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  emitChildClick(listing: GroupListingViewModel) {
    console.log("listing emitted: ", listing.listingTitle);
    this.listingForModal.emit(listing);
  }

  changeSelectedFromChild(selected: SelectionModel<GroupListingViewModel>) {
    this.selection = selected;
  }

  loadBookmarks(pageIdx: number, pageSize: number) {
    this.userBms.getBookmarks(pageIdx, pageSize)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (page) => {
          this.dataSource.data = page.content;
          this.totalElements = page.totalElements;
          this.pageSize = page.size;
          this.pageIndex = page.number;
          this.noResults = (this.dataSource.data.length === 0);
        }
      })
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
}
