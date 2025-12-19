import {AfterViewInit, Component, EventEmitter, inject, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable,
  MatTableDataSource
} from "@angular/material/table";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {AsyncPipe, DatePipe, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatMenu, MatMenuTrigger} from "@angular/material/menu";
import {BookmarkApiService} from "../../services/api-services/bookmarks-api/bookmark-api.service";
import {map, shareReplay, Subject, takeUntil} from "rxjs";
import {MatCheckbox} from "@angular/material/checkbox";
import {BreakpointObserver} from "@angular/cdk/layout";
import {SelectionModel} from "@angular/cdk/collections";
import {environment} from "../../../environments/environment";
import {CloseValue} from "../group-listing-modal/group-listing-modal.component";
import {
  ListingViewInteractionsService
} from "../../services/facade-services/listing-view-interactions/listing-view-interactions.service";
import {MatPaginator, PageEvent} from "@angular/material/paginator";

@Component({
  selector: 'app-user-bookmarks-table',
  standalone: true,
  templateUrl: './user-bookmarks-table.component.html',
  imports: [
    DatePipe,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatIconButton,
    MatMenu,
    MatRow,
    MatRowDef,
    MatTable,
    NgIf,
    MatMenuTrigger,
    MatHeaderCellDef,
    AsyncPipe,
    MatCheckbox,
    MatPaginator
  ],
  styleUrl: './user-bookmarks-table.component.css'
})

export class UserBookmarksTableComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  private destroy$: Subject<void> = new Subject<void>();
  private breakpointObserver = inject(BreakpointObserver);
  protected listingInteract = inject(ListingViewInteractionsService)

  @Output() listingForModal = new EventEmitter<GroupListingViewModel>();

  normalColumns = [ 'select', 'title', 'status', 'category', 'pvp', 'system', 'roles', 'updated' ]
  mobileColumns = ['options', 'title', 'updated']
  dataSource = new MatTableDataSource<GroupListingViewModel>();
  selection = new SelectionModel<GroupListingViewModel>(true, [])
  noResults: boolean = true;

  pageIndex = 0;
  pageSize = 10;
  totalElements = 0;

  constructor(private userBms: BookmarkApiService) {
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

  // check whether the number of selected rows matches total rows
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  singleSelected() {
    // if(!environment.production) {
    //   console.log("HERE IS THE USER ACCT TABLE SELECTED LISTING DATA: ", this.selection.selected);
    // }
    return this.selection.selected.length < 2;
  }

  toggleAllRows() {
    if(this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  checkboxLabel(row?: GroupListingViewModel){
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.groupId + 1}`
  }

  onRowClick(listing: GroupListingViewModel) {
    this.listingForModal.emit(listing);
  }

  isMobile$ = this.breakpointObserver
    .observe('(max-width: 499px)')
    .pipe(map(result => result.matches),
      shareReplay());
}
