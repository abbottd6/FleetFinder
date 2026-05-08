import {AfterViewInit, Component, ElementRef, EventEmitter, inject, OnDestroy, Output, ViewChild} from '@angular/core';
import {AsyncPipe, DatePipe, NgIf, SlicePipe} from "@angular/common";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {map, Observable, shareReplay, Subject, takeUntil} from "rxjs";
import {BreakpointObserver} from "@angular/cdk/layout";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource
} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import {
  ListingTemplatesApiService
} from "../../services/api-services/listing-templates-api/listing-templates-api.service";
import {
  ListingViewInteractionsService
} from "../../services/facade-services/listing-view-interactions/listing-view-interactions.service";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {LayoutMode} from "../input-fields/search-bar/search-bar.component";
import {MatCheckbox} from "@angular/material/checkbox";
import {ListingTemplateViewModel} from "../../models/listing-templates/listing-template-view-model";
import {MatSort, Sort} from "@angular/material/sort";
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {Router} from "@angular/router";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmGenericComponent} from "../pop-ups/confirm-generic/confirm-generic.component";
import {TemplatesModalService} from "../../services/component-services/templates-modal-service/templates-modal.service";
import {Page} from "../../models/page-interface";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-user-profile-templates',
  standalone: true,
  templateUrl: './user-profile-templates.component.html',
  imports: [
    AsyncPipe,
    MatPaginator,
    NgIf,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger,
    DatePipe,
    MatCell,
    MatCellDef,
    MatCheckbox,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatTable,
    MatHeaderCellDef,
    MatSort,
    SlicePipe,
    MatIcon,
  ],
  styleUrl: './user-profile-templates.component.css'
})
export class UserProfileTemplatesComponent implements AfterViewInit, OnDestroy {
  private breakpointObserver = inject(BreakpointObserver);
  private templatesPanelDestroy$: Subject<void> = new Subject<void>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  private _liveAnnouncer = inject(LiveAnnouncer);

  @ViewChild(MatMenuTrigger) templatesMenuTrigger!: MatMenuTrigger;
  @ViewChild('templatesMenuAnchor', { read: ElementRef })
  protected templatesMenuAnchor!: ElementRef<HTMLElement>;
  longPressTimer: any;
  private readonly LONG_PRESS_MS = 500;
  public longPressTriggered = false;

  @Output() templateForModal = new EventEmitter<ListingTemplateViewModel>();

  fullColumns = [ 'select', 'title', 'status', 'category', 'pvp', 'system', 'roles', 'created' ];
  mobileColumns = ['details'];
  readonly displayedColumns$!: Observable<string[]>;

  dataSource = new MatTableDataSource<ListingTemplateViewModel>();
  selection = new SelectionModel<ListingTemplateViewModel>(false, [])
  clickedTemplate : ListingTemplateViewModel | null = null;
  noResults: boolean = true;

  pageIdx: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  sortActive: string = 'creationTimestamp';
  sortDirection: string = 'desc';

  constructor(private templatesApi: ListingTemplatesApiService,
              protected listingInteract: ListingViewInteractionsService,
              private templatesModalSrv: TemplatesModalService,
              private router: Router,
              private snackBar: MatSnackBar,
              private dialog: MatDialog) {

    this.loadTemplates();

    this.templatesModalSrv.refresh$.pipe(takeUntil(this.templatesPanelDestroy$))
      .subscribe(reason => {
      if(reason === 'delete') {
        this.loadTemplates();
      }
    })

    this.displayedColumns$ = this.layoutMode$.pipe(takeUntil(this.templatesPanelDestroy$)).pipe(
      map(mode => mode === 'handheld' ? this.mobileColumns : this.fullColumns)
    );
  }

  ngAfterViewInit() {
    this.paginator.page.pipe(takeUntil(this.templatesPanelDestroy$))
      .subscribe((event: PageEvent) => {
        this.pageIdx = event.pageIndex;
        this.pageSize = event.pageSize;
        this.loadTemplates();
      })

    this.templatesMenuTrigger.menuClosed
      .pipe(takeUntil(this.templatesPanelDestroy$))
      .subscribe(menuClosed => {
        this.clickedTemplate = null;
      })
  }

  ngOnDestroy() {
    this.templatesPanelDestroy$.next();
    this.templatesPanelDestroy$.complete();
  }

  loadTemplates() {
    this.templatesApi.getTemplates(this.pageIdx, this.pageSize, this.sortDirection, this.sortActive)
      .pipe(takeUntil(this.templatesPanelDestroy$))
      .subscribe({
        next: (page: Page<ListingTemplateViewModel>) => {
          this.dataSource.data = page.content;
          this.totalElements = page.page.totalElements;
          this.pageSize = page.page.size;
          this.pageIdx = page.page.number;
          this.noResults = (this.dataSource.data.length === 0);
        }
      });
  }

  createFromTemplate() {
    const template = this.clickedTemplate ?? this.selection.selected[0];

    this.router.navigate(['/create-listing'], {
      state: { draft: template }
    });
  }

  openConfirmDelete() {
    const template = this.clickedTemplate ?? this.selection.selected[0];
    const message: string = "Please confirm deletion of:"
    const dialogRef = this.dialog.open(ConfirmGenericComponent, {
      data: {
        message: message,
        title: template.listingTitle
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if(result == true) {
        this.deleteTemplate(template.templateId);
      }
    })
  }

  deleteTemplate(templateId: number) {
    this.templatesApi.deleteTemplate(templateId).pipe(takeUntil(this.templatesPanelDestroy$))
      .subscribe({
        next: (response: {message: string }) => {
          this.snackBar.open(`${response.message}`, 'OK', {
            duration: 4000,
            verticalPosition: 'top',
            horizontalPosition: 'center',
            panelClass: ['mobile-snackbar']
          });
          this.selection.clear();
          this.clickedTemplate = null;
          this.loadTemplates();
        },
        error: err => {
          alert(`There was an error deleting the template: ${err.message}`);
        }
      })
  }

  announceSortChange(sortState: Sort) {
    if (sortState.direction) {
      this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
    } else {
      this._liveAnnouncer.announce('Sorting cleared');
    }
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

  onRowClick(template: ListingTemplateViewModel) {
    this.templateForModal.emit(template)
  }

  /* ----------------------------------- CONTEXT MENU ----------------------------------------------------------------*/
  openContextMenu(event: MouseEvent, row: ListingTemplateViewModel) {
    event.preventDefault();
    this.clickedTemplate = row;

    this.openMenuAt(event.clientX, event.clientY);
  }

  openMenuAt(x: number, y: number) {
    if (!this.templatesMenuAnchor || this.templatesMenuTrigger == undefined) return;
    const el = this.templatesMenuAnchor.nativeElement;

    el.style.left = `${x}px`;
    el.style.top = `${y}px`;
    queueMicrotask(() => this.templatesMenuTrigger.openMenu());
  }

  onTouchStart(event: TouchEvent, row: ListingTemplateViewModel) {
    if(event.touches.length !== 1) return;
    this.clickedTemplate = row;
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

  /*------------------------------------- SELECT CHECKBOX COLUMN -----------------------------------------------------*/
  toggleAllRows() {
    if(this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  checkboxLabel(row?: ListingTemplateViewModel){
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.templateId + 1}`
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
