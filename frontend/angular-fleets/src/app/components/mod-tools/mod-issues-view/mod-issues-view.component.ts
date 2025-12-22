import {AfterViewInit, Component, EventEmitter, inject, OnDestroy, Output, ViewChild} from '@angular/core';
import {map, Observable, shareReplay, Subject, takeUntil} from "rxjs";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
  MatTable,
  MatTableDataSource
} from "@angular/material/table";
import {ModIssueViewModel} from "../../../models/moderation/ModIssueViewModel";
import {ModApiService} from "../../../services/api-services/mod-api/mod-api.service";
import {
  GroupListingFetchService,
  Page
} from "../../../services/api-services/group-listings-fetch-api/group-listing-fetch.service";
import {LayoutMode} from "../../input-fields/search-bar/search-bar.component";
import {BreakpointObserver} from "@angular/cdk/layout";
import {UiPrefsService} from "../../../services/facade-services/ui-prefs/ui-prefs.service";
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {MatSort, Sort} from "@angular/material/sort";
import {AsyncPipe, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {SelectionModel} from "@angular/cdk/collections";
import {MatCheckbox} from "@angular/material/checkbox";

@Component({
  selector: 'app-mod-issues-view',
  standalone: true,
  templateUrl: './mod-issues-view.component.html',
  imports: [
    MatTable,
    MatSort,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCell,
    MatCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRowDef,
    MatRow,
    MatPaginator,
    NgIf,
    AsyncPipe,
    MatIcon,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger,
    MatCheckbox
  ],
  styleUrl: './mod-issues-view.component.css'
})
export class ModIssuesViewComponent implements OnDestroy, AfterViewInit {
  private issuesPanelDestroy$ = new Subject<void>();
  private breakpointObserver = new BreakpointObserver();

  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  private _liveAnnouncer = inject(LiveAnnouncer);

  @Output() listingToEmit = new EventEmitter<GroupListingViewModel>();

  fullColumns: string[] = ['select', 'username', 'userId', 'status', 'reportsTotal', 'spam', 'hate', 'nsfw', 'scam',
    'offTopic', 'troll', 'doxx', 'cheat', 'other']

  mobileColumns: string[] = ['username', 'userId', 'status', 'reportsTotal']

  dataSource = new MatTableDataSource<ModIssueViewModel>();
  selection = new SelectionModel<ModIssueViewModel>(true, [])
  noResults: boolean = true;

  pageIndex: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  sortActive: string = 'reportTotalCount';
  sortDirection: string = 'desc';

  constructor(private modApi: ModApiService,private uiPrefService: UiPrefsService,
              private gls: GroupListingFetchService){
    this.loadIssues(this.pageIndex, this.pageSize, this.sortActive, this.sortDirection);

    this.uiPrefService.uiPrefs = this.uiPrefService.loadUiPrefs();
  }

  ngAfterViewInit(){
    this.paginator.page.pipe(takeUntil(this.issuesPanelDestroy$))
      .subscribe((event: PageEvent) => {
        this.pageIndex = event.pageIndex;
        this.pageSize = event.pageSize;
        this.loadIssues(this.pageIndex, this.pageSize, this.sortActive, this.sortDirection);
      })
  }

  ngOnDestroy() {
    this.issuesPanelDestroy$.next();
    this.issuesPanelDestroy$.complete();
  }

  loadIssues(idx: number, sz: number, field: string, dir: string) {
    this.modApi.modGetIssues(idx, sz, field, dir)
      .pipe(takeUntil(this.issuesPanelDestroy$))
      .subscribe({
        next: (page: Page<ModIssueViewModel>) => {
          this.dataSource.data = page.content;
          this.totalElements = page.totalElements;
          this.pageSize = page.size;
          this.pageIndex = page.number;
          this.noResults = (this.dataSource.data.length === 0);
        }
      });
  }

  announceSortChange(sortState: Sort) {
    if (sortState.direction) {
      this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
    } else {
      this._liveAnnouncer.announce('Sorting cleared');
    }
  }

  emitClick(issue: ModIssueViewModel) {
    this.gls.getGroupById(issue.groupId).subscribe({
      next: (clicked) => this.listingToEmit.emit(clicked),
      error: (err) => console.error('Return group from mod issue groupId failed', err),
    });
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

  /*------------------------------------- SELECT CHECKBOX COLUMN -----------------------------------------------------*/
  toggleAllRows() {
    if(this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  checkboxLabel(row?: ModIssueViewModel){
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.groupId + 1}`
  }

  // check whether the number of selected rows matches total rows
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  singleSelected() {
    return this.selection.selected.length === 1;
  }
}
