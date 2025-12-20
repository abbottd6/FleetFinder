import {Component, ElementRef, EventEmitter, inject, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
  MatTable,
  MatTableDataSource
} from "@angular/material/table";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {MatCheckbox} from "@angular/material/checkbox";
import {AsyncPipe, DatePipe, NgIf, SlicePipe} from "@angular/common";
import {
  ListingViewInteractionsService
} from "../../../services/facade-services/listing-view-interactions/listing-view-interactions.service";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {UiPrefsService} from "../../../services/facade-services/ui-prefs/ui-prefs.service";
import {TooltipPosition} from "@angular/material/tooltip";
import {SelectionModel} from "@angular/cdk/collections";
import {BehaviorSubject, Subject, takeUntil} from "rxjs";
import {AuthService} from "../../../services/auth/auth-services/auth.service";

@Component({
  selector: 'app-mobile-feed-view',
  standalone: true,
  templateUrl: './mobile-feed-view.component.html',
  imports: [
    MatTable,
    MatCheckbox,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCell,
    MatCellDef,
    DatePipe,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    NgIf,
    SlicePipe,
    AsyncPipe,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger
  ],
  styleUrl: './mobile-feed-view.component.css'
})
export class MobileFeedViewComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() dataSource!: MatTableDataSource<GroupListingViewModel>;
  @Input() columns!: string[];
  @Output() listingForModal = new EventEmitter<GroupListingViewModel>();
  @Output() theseSelected = new EventEmitter<SelectionModel<GroupListingViewModel>>();

  @ViewChild(MatMenuTrigger) menuTrigger!: MatMenuTrigger;
  @ViewChild('contextMenuAnchor', { read: ElementRef })
  private contextMenuAnchor!: ElementRef<HTMLElement>;
  longPressTimer: any;
  private readonly LONG_PRESS_MS = 400;


  protected listingInteract = inject(ListingViewInteractionsService);
  protected uiPrefService = inject(UiPrefsService);
  protected auth = inject(AuthService);
  positionOptions: TooltipPosition[] = ['after', 'before', 'above', 'below', 'left', 'right'];

  selection = new SelectionModel<GroupListingViewModel>(true, [])
  noResults: boolean = true;


  ngOnInit(): void {
    this.uiPrefService.uiPrefs = this.uiPrefService.loadUiPrefs();

    this.auth.isLoggedIn$.pipe(takeUntil(this.destroy$)).subscribe(
      val => this.listingInteract.isLoggedIn = val);

    if(this.columns.includes('select')) {
      this.selection.changed.pipe(takeUntil(this.destroy$)).subscribe(change => {
        this.theseSelected.emit(this.selection);
      })
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  listingSelected(listing: GroupListingViewModel) {
    console.log("listing emitter from child child.")
    this.saveClick(listing.groupId);
    this.listingForModal.emit(listing)
  }

  /* ------------------------------------------ QUICK ACCESS MENU ----------------------------------------------------*/

  openContextMenu(event: MouseEvent, row: GroupListingViewModel) {
    event.preventDefault();
    this.listingInteract.setSelectedListing(row);

    this.openMenuAt(event.clientX, event.clientY);
  }

  openMenuAt(x: number, y: number) {
    const el = this.contextMenuAnchor.nativeElement;

    el.style.left = `${x}px`;
    el.style.top = `${y}px`;

    queueMicrotask(() => this.menuTrigger.openMenu());
  }

  onTouchStart() {
    this.listingInteract.longPressTriggered = false;
  }

  onTouchEnd(event: TouchEvent, row: GroupListingViewModel) {
    if(event.touches.length !== 1) return;

    event.preventDefault();
    this.listingInteract.setSelectedListing(row)

    const touch = event.touches[0];
    this.longPressTimer = setTimeout(() => {
      this.listingInteract.longPressTriggered = true;
      this.openMenuAt(touch.clientX, touch.clientY);
    }, this.LONG_PRESS_MS);
    clearTimeout(this.longPressTimer);
  }

  /*------------------------------------- SELECT CHECKBOX COLUMN -----------------------------------------------------*/
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

  // check whether the number of selected rows matches total rows
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /* ------------------------------------ INTERFACE TO UI PREFS SERVICE ----------------------------------------------*/

  protected saveClick(groupId: number) {
    this.uiPrefService.saveRowClick(groupId);
  }

  isRowClicked(row: GroupListingViewModel): boolean {
    return this.uiPrefService.uiPrefs.clickedRowIds.has(row.groupId);
  }
}
