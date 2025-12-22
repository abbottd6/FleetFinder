import {Component, EventEmitter, inject, Input, OnChanges, OnInit, Output} from '@angular/core';
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {environment} from "../../../../environments/environment";
import {ModApiService} from "../../../services/api-services/mod-api/mod-api.service";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
  MatTable,
  MatTableDataSource
} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Router} from "@angular/router";
import {UserService} from "../../../services/user-services/user.service";
import {MatCheckbox} from "@angular/material/checkbox";
import {AsyncPipe, DatePipe, NgIf} from "@angular/common";
import {map, shareReplay} from "rxjs";
import {BreakpointObserver} from "@angular/cdk/layout";

@Component({
  selector: 'app-mod-listings-table',
  standalone: true,
  templateUrl: './mod-listings-table.component.html',
  imports: [
    MatTable,
    MatCheckbox,
    MatCell,
    MatColumnDef,
    MatHeaderCell,
    MatCellDef,
    MatHeaderCellDef,
    DatePipe,
    MatHeaderRow,
    MatRow,
    MatRowDef,
    MatHeaderRowDef,
    AsyncPipe,
    NgIf
  ],
  styleUrl: '../../user-acct-listings-table/user-acct-listings-table.component.css'
})
export class ModListingsTableComponent implements OnInit {
  @Input() public modGroupListings: GroupListingViewModel[] = [];
  @Output() listingForModal = new EventEmitter<GroupListingViewModel>();
  private breakpointObserver = inject(BreakpointObserver);
  selectedListing: GroupListingViewModel | null = null;
  isModalVisible: boolean = false;

  largeColumns = [ 'select', 'title', 'status', 'category', 'pvp', 'system', 'roles', 'updated' ]
  mobileColumns = ['select', 'title', 'updated']
  selection = new SelectionModel<GroupListingViewModel>(true, []);

  constructor(private modService: ModApiService, private router: Router, private snackBar: MatSnackBar) {
    console.log("THE DATA: ", this.modGroupListings);
  }

  ngOnInit() {
    this.loadGroupListings()
  }

  //on-row-click instructions for groupListing modal popup
  onRowClick(tempListing: GroupListingViewModel) {
    this.listingForModal.emit(tempListing);
    if(!environment.production) {
      console.log("HERE IS THE LISTING DATA: ", tempListing);
    }
  }

  // check whether the number of selected rows matches total rows
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.modGroupListings.length;
    return numSelected === numRows;
  }

  singleSelected() {
    if(!environment.production) {
      console.log("HERE IS THE USER ACCT TABLE SELECTED LISTING DATA: ", this.selection.selected);
    }
    return this.selection.selected.length < 2;
  }

  toggleAllRows() {
    if(this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.modGroupListings);
  }

  checkboxLabel(row?: GroupListingViewModel){
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.groupId + 1}`
  }

  tableActionReset() {
    this.selection.clear();
    this.modService.refreshModPanel();
    this.loadGroupListings();

    this.router.navigateByUrl("/app-user");
  }

  modDeleteListing() {
    const selectedCount = this.selection.selected.length;
    for (let i = 0; i < this.selection.selected.length; i++) {
      this.modService.modDeleteListing(this.selection.selected[i].groupId).subscribe({
        next: response => {
          if(!environment.production) {
            console.log(response.listingTitle)
          }
          this.tableActionReset()
        },
        error: err => {
          alert(`There was an error deleting this listing: ${err.message}`);
        }
      });
    }
    this.snackBar.open(`You successfully deleted [${selectedCount}] listing(s).`, 'OK',
      {duration: 6000, verticalPosition: 'top', horizontalPosition: 'center', panelClass: ['my-snackbar']});
  }

  loadGroupListings() {
    this.modService.modGetGroupListings().subscribe({
      next: (data: GroupListingViewModel[]) => {
        if(!environment.production) {
          console.log('Data received in component:', data);
        }
        this.modGroupListings = data;
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

  isMobile$ = this.breakpointObserver
    .observe('(min-width: 499px)')
    .pipe(map(result => result.matches),
      shareReplay());
}
