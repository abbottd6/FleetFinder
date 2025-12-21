import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  inject,
  Input,
  OnDestroy, OnInit,
  Output,
  ViewChild
} from '@angular/core';
import {
  MatCell, MatCellDef, MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef, MatTable, MatTableDataSource
} from "@angular/material/table";
import {AsyncPipe, DatePipe, NgIf} from "@angular/common";
import {MatCheckbox} from "@angular/material/checkbox";
import {Subject, takeUntil} from "rxjs";
import {GroupListingViewModel} from "../../../../models/group-listing/group-listing-view-model";
import {SelectionModel} from "@angular/cdk/collections";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {
  ListingViewInteractionsService
} from "../../../../services/facade-services/listing-view-interactions/listing-view-interactions.service";
import {UiPrefsService} from "../../../../services/facade-services/ui-prefs/ui-prefs.service";
import {AuthService} from "../../../../services/auth/auth-services/auth.service";
import {
  QuickAccessMenuService
} from "../../../../services/component-services/quick-access-menu/quick-access-menu.service";
import {TooltipPosition} from "@angular/material/tooltip";
import {
  ListingOwnerActionsService
} from "../../../../services/facade-services/listing-view-interactions/listing-owner-actions.service";

@Component({
  selector: 'app-desktop-table-view',
  standalone: true,
  templateUrl: './desktop-table-view.component.html',
  imports: [
    MatHeaderRow,
    MatRow,
    MatHeaderRowDef,
    MatRowDef,
    MatCell,
    MatHeaderCell,
    DatePipe,
    MatHeaderCellDef,
    MatCellDef,
    MatColumnDef,
    MatCheckbox,
    MatTable,
    AsyncPipe,
    MatMenu,
    MatMenuItem,
    NgIf,
    MatMenuTrigger
  ],
  styleUrl: './desktop-table-view.component.css'
})
export class DesktopTableViewComponent implements OnInit, OnDestroy, AfterViewInit {
  private desktopDestroy$ = new Subject<void>();

  @Input() dataSource!: MatTableDataSource<GroupListingViewModel>;
  @Input() columns!: string[];
  @Output() listingToEmit = new EventEmitter<GroupListingViewModel>();
  @Output() selected = new EventEmitter<SelectionModel<GroupListingViewModel>>();

  @ViewChild(MatMenuTrigger) desktopTrigger!: MatMenuTrigger;
  @ViewChild('desktopMenuAnchor', { read: ElementRef })
  protected desktopMenuAnchor!: ElementRef<HTMLElement>;

  constructor(protected listingInteract: ListingViewInteractionsService,
              protected uiPrefService: UiPrefsService,
              protected auth: AuthService,
              protected desktopQuickMenu: QuickAccessMenuService,
              protected ownerService: ListingOwnerActionsService) {}

  positionOptions: TooltipPosition[] = ['after', 'before', 'above', 'below', 'left', 'right'];

  selection = new SelectionModel<GroupListingViewModel>(true, [])

  ngOnInit(): void {
    this.uiPrefService.uiPrefs = this.uiPrefService.loadUiPrefs();

    this.auth.isLoggedIn$.pipe(takeUntil(this.desktopDestroy$)).subscribe(
      val => this.listingInteract.isLoggedIn = val);

    if(this.columns.includes('select')) {
      this.selection.changed.pipe(takeUntil(this.desktopDestroy$)).subscribe(change => {
        this.selected.emit(this.selection);
      })
    }
  }

  ngAfterViewInit() {
    this.desktopQuickMenu.registerMenu(this.desktopTrigger, this.desktopMenuAnchor)
  }

  ngOnDestroy() {
    this.desktopDestroy$.next();
    this.desktopDestroy$.complete();
  }

  listingSelected(listing: GroupListingViewModel) {
    this.saveClick(listing.groupId);
    this.listingToEmit.emit(listing)
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
