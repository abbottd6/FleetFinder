import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  inject,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
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
import {Subject, takeUntil} from "rxjs";
import {AuthService} from "../../../services/auth/auth-services/auth.service";
import {QuickAccessMenuService} from "../../../services/component-services/quick-access-menu/quick-access-menu.service";
import {
  ListingOwnerActionsService
} from "../../../services/facade-services/listing-view-interactions/listing-owner-actions.service";
import {ChatHostService} from "../../../services/facade-services/chat/chat-host.service";

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
export class MobileFeedViewComponent implements OnInit, OnDestroy, AfterViewInit {
  private mobileDestroy$ = new Subject<void>();

  @Input() dataSource!: MatTableDataSource<GroupListingViewModel>;
  @Input() columns!: string[];
  @Output() listingForModal = new EventEmitter<GroupListingViewModel>();
  @Output() theseSelected = new EventEmitter<SelectionModel<GroupListingViewModel>>();

  @ViewChild(MatMenuTrigger) menuTrigger!: MatMenuTrigger;
  @ViewChild('contextMenuAnchor', { read: ElementRef })
  protected contextMenuAnchor!: ElementRef<HTMLElement>;

  positionOptions: TooltipPosition[] = ['after', 'before', 'above', 'below', 'left', 'right'];

  selection = new SelectionModel<GroupListingViewModel>(true, [])

  constructor(protected listingInteract: ListingViewInteractionsService,
              protected uiPrefService: UiPrefsService,
              protected auth: AuthService,
              protected quickMenu: QuickAccessMenuService,
              protected ownerService: ListingOwnerActionsService,
              protected chatHostSrv: ChatHostService) {}

  ngOnInit(): void {
    this.uiPrefService.uiPrefs = this.uiPrefService.loadUiPrefs();

    this.auth.isLoggedIn$.pipe(takeUntil(this.mobileDestroy$)).subscribe(
      val => this.listingInteract.isLoggedIn = val);

    if(this.columns.includes('select')) {
      this.selection.changed.pipe(takeUntil(this.mobileDestroy$)).subscribe(change => {
        this.theseSelected.emit(this.selection);
      })
    }
  }

  ngAfterViewInit() {
    this.quickMenu.registerMenu(this.menuTrigger, this.contextMenuAnchor)
  }

  ngOnDestroy() {
    this.mobileDestroy$.next();
    this.mobileDestroy$.complete();
  }

  listingSelected(listing: GroupListingViewModel) {
    this.saveClick(listing.groupId);
    this.listingForModal.emit(listing)
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
