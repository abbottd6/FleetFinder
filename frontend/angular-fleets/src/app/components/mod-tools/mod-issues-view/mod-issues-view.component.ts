import {AfterViewInit, Component, ElementRef, EventEmitter, inject, OnDestroy, Output, ViewChild} from '@angular/core';
import {firstValueFrom, map, Observable, shareReplay, Subject, take, takeUntil} from "rxjs";
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
  GroupListingFetchService
} from "../../../services/api-services/group-listings-fetch-api/group-listing-fetch.service";
import {LayoutMode} from "../../input-fields/search-bar/search-bar.component";
import {BreakpointObserver} from "@angular/cdk/layout";
import {UiPrefsService} from "../../../services/facade-services/ui-prefs/ui-prefs.service";
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {MatSort, MatSortHeader, Sort} from "@angular/material/sort";
import {AsyncPipe, DatePipe, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {SelectionModel} from "@angular/cdk/collections";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatDialog} from "@angular/material/dialog";
import {ListingReportApiService} from "../../../services/api-services/listing-reports-api/listing-report-api.service";
import {ConfirmReportComponent} from "../../pop-ups/confirm-report/confirm-report.component";
import {ConfirmClearIssueComponent} from "../../pop-ups/confirm-clear-issue/confirm-clear-issue.component";
import {ModIssueDetailedComponent} from "../../pop-ups/mod-issue-detailed/mod-issue-detailed.component";
import {
  ListingViewInteractionsService
} from "../../../services/facade-services/listing-view-interactions/listing-view-interactions.service";
import {Page} from "../../../models/page-interface";

@Component({
  selector: 'app-mod-issues-view',
  standalone: true,
  templateUrl: './mod-issues-view.component.html',
  imports: [
    MatTable,
    MatSort,
    MatSortHeader,
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
    MatCheckbox,
    DatePipe
  ],
  styleUrl: './mod-issues-view.component.css'
})
export class ModIssuesViewComponent implements OnDestroy, AfterViewInit {
  private issuesPanelDestroy$ = new Subject<void>();
  private breakpointObserver = new BreakpointObserver();

  @ViewChild(MatMenuTrigger) issuesMenuTrigger!: MatMenuTrigger;
  @ViewChild('issuesMenuAnchor', { read: ElementRef })
  protected issuesMenuAnchor!: ElementRef<HTMLElement>;
  longPressTimer: any;
  private readonly LONG_PRESS_MS = 500;
  public longPressTriggered = false;

  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  private _liveAnnouncer = inject(LiveAnnouncer);

  @Output() listingToEmit = new EventEmitter<GroupListingViewModel>();

  fullColumns: string[] = ['select', 'username', 'userId', 'status', 'reportsTotal', 'firstR', 'lastR']
  mobileColumns: string[] = ['select', 'username', 'reportsTotal', 'lastR']
  readonly displayedColumns$!: Observable<string[]>;

  dataSource = new MatTableDataSource<ModIssueViewModel>();
  selection = new SelectionModel<ModIssueViewModel>(false, [])
  clickedIssue!: ModIssueViewModel;
  noResults: boolean = true;

  pageIndex: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  sortActive: string = 'reportTotalCount';
  sortDirection: string = 'desc';

  constructor(private modApi: ModApiService,
              private uiPrefService: UiPrefsService,
              private gls: GroupListingFetchService,
              private reportsApi: ListingReportApiService,
              private listingInteract: ListingViewInteractionsService,
              private snackBar: MatSnackBar,
              private dialog: MatDialog){
    this.loadIssues(this.pageIndex, this.pageSize, this.sortActive, this.sortDirection);

    this.uiPrefService.uiPrefs = this.uiPrefService.loadUiPrefs();

    this.displayedColumns$ = this.layoutMode$.pipe(
      map(mode => mode === 'handheld' ? this.mobileColumns : this.fullColumns)
    );
  }

  ngAfterViewInit(){
    this.paginator.page.pipe(takeUntil(this.issuesPanelDestroy$))
      .subscribe((event: PageEvent) => {
        this.pageIndex = event.pageIndex;
        this.pageSize = event.pageSize;
        this.loadIssues(this.pageIndex, this.pageSize, this.sortActive, this.sortDirection);
      })

    this.sort.sortChange.pipe(takeUntil(this.issuesPanelDestroy$))
      .subscribe((event: Sort) => {
        this.sortActive = event.active;
        this.sortDirection = event.direction || 'desc';

        this.pageIndex = 0;

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
          this.totalElements = page.page.totalElements;
          this.pageSize = page.page.size;
          this.pageIndex = page.page.number;
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
      next: (clicked) => {
        this.listingInteract.setSelectedListing(clicked);
        this.listingToEmit.emit(clicked);
      },
      error: (err) => console.error('Return group from mod issue groupId failed', err),
    });
  }

  openConfirmDelete(issue: ModIssueViewModel) {
    const action: string = 'deletion';

    let subject: GroupListingViewModel;

    this.gls.getGroupById(issue.groupId).pipe(take(1))
      .subscribe(listing => subject = listing);

    this.reportsApi.reportOptions$
      .pipe(take(1))
      .subscribe(options => {
        const dialogRef = this.dialog.open(ConfirmReportComponent, {
          data: {
            action,
            subject,
            options
          }
        });

        dialogRef.afterClosed().pipe(takeUntil(this.issuesPanelDestroy$)).subscribe(result => {
          if (result) {
            this.modDeleteListing(subject, result)
          }
        })
      })
  }

  openClearReports(issue: ModIssueViewModel) {
    this.gls.getGroupById(issue.groupId).pipe(take(1))
      .subscribe({
        next: subject => {
          const dialogRef = this.dialog.open(ConfirmClearIssueComponent, {
            maxWidth: '95vw',
            minWidth: '30vw',
            data: {
              title: subject.listingTitle
            }
          });

          dialogRef.afterClosed().pipe(takeUntil(this.issuesPanelDestroy$)).subscribe(result => {
            if(result != null) {
              this.modClearReportCounts(issue.issueId, result);
            }
          })
        }
    });
  }

  modClearReportCounts(issueId: number, note: string) {
    this.modApi.modClearIssue(issueId, note).pipe(takeUntil(this.issuesPanelDestroy$)).subscribe({
      next: (response: { message: string; }) => {
        this.snackBar.open( `${response.message}`, 'OK', {
          duration: 4000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['mobile-snackbar']
        });
        this.loadIssues(this.pageIndex, this.pageSize, this.sortActive, this.sortDirection);
      },
      error: err => {
        alert(`There was an error clearing the report counts: ${err.message}`);
      }
    })
  }

  modDeleteListing(listing: GroupListingViewModel, basis: number) {
    const listingId = listing.groupId;
    const title = listing.listingTitle;

    if(!this.singleSelected()) return;

    this.modApi.modDeleteListing(listingId, basis).subscribe({
      next: response => {
        this.selection.clear();
        this.loadIssues(this.pageIndex, this.pageSize, this.sortActive, this.sortDirection);
      },
      error: err => {
        alert(`There was an error deleting this listing: ${err.message}`);
      }
    });

    this.snackBar.open(`Successful mod deletion of ${title}.`, 'OK',
      {duration: 6000, verticalPosition: 'top', horizontalPosition: 'center', panelClass: ['my-snackbar']});
  }

  openDetailedIssue() {
    if(!this.clickedIssue) return;

    this.gls.getGroupById(this.clickedIssue.groupId).pipe(take(1))
      .subscribe( {
        next: subject => {
          this.dialog.open(ModIssueDetailedComponent, {
            maxWidth: '95vw',
            minWidth: '30vw',
            data: {
              issue: this.clickedIssue,
              listing: subject
            }
          })
        }
      });
  }

  openContextMenu(event: MouseEvent, row: ModIssueViewModel) {
    event.preventDefault();
    this.clickedIssue = row;

    this.openMenuAt(event.clientX, event.clientY);
  }

  openMenuAt(x: number, y: number) {
    if (!this.issuesMenuAnchor || this.issuesMenuTrigger == undefined) return;
    const el = this.issuesMenuAnchor.nativeElement;

    el.style.left = `${x}px`;
    el.style.top = `${y}px`;
    queueMicrotask(() => this.issuesMenuTrigger.openMenu());
  }

  onTouchStart(event: TouchEvent, row: ModIssueViewModel) {
    if(event.touches.length !== 1) return;
    this.clickedIssue = row;
    this.longPressTriggered = false;

    const touch = event.touches[0];
    this.longPressTimer = setTimeout(() => {
      this.longPressTriggered = true;
      if(this.longPressTimer >= this.LONG_PRESS_MS) {
        event.preventDefault();
      }
      this.openMenuAt(touch.clientX, touch.clientY);
    }, this.LONG_PRESS_MS);
  }

  onTouchEnd(event: TouchEvent) {
    if(this.longPressTriggered) {
      event.preventDefault();
    }
    clearTimeout(this.longPressTimer);
    this.longPressTriggered = false;
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
