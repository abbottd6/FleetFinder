import {Component, EventEmitter, inject, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatTableDataSource, MatTableModule} from "@angular/material/table";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {SelectionModel} from "@angular/cdk/collections";
import {AsyncPipe, DatePipe, NgIf} from "@angular/common";
import {Router, RouterLink} from "@angular/router";
import {UserListingService} from "../../services/group-listing-services/user-listing.service";
import {UserService} from "../../services/user-services/user.service";
import {environment} from "../../../environments/environment";
import {map, shareReplay} from "rxjs";
import {BreakpointObserver} from "@angular/cdk/layout";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatMenu, MatMenuTrigger} from "@angular/material/menu";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmDeleteComponent} from "../pop-ups/confirm-delete/confirm-delete.component";
import {GroupListingsComponent} from "../group-listings/group-listings.component";

@Component({
  selector: 'app-user-acct-listings-table',
  standalone: true,
  templateUrl: './user-acct-listings-table.component.html',
  styleUrl: './user-acct-listings-table.component.css',
  imports: [MatTableModule, MatCheckboxModule, DatePipe, RouterLink, AsyncPipe, NgIf, MatIcon, MatIconButton, MatMenu, MatMenuTrigger],
})
export class UserAcctListingsTableComponent implements OnChanges {
  @Input() userListings: GroupListingViewModel[] = [];
  @Output() listingForModal = new EventEmitter<GroupListingViewModel>();
  private breakpointObserver = inject(BreakpointObserver);
  readonly dialog = inject(MatDialog);

  largeColumns = [ 'select', 'title', 'status', 'category', 'pvp', 'system', 'roles', 'updated' ]
  mobileColumns = ['options', 'title', 'updated']
  dataSource = new MatTableDataSource(this.userListings);
  selection = new SelectionModel<GroupListingViewModel>(true, []);

  constructor(private userListingService: UserListingService, private router: Router, private userService: UserService,
              private snackBar: MatSnackBar) {}

  ngOnChanges(changes: SimpleChanges) {
    this.dataSource.data = this.userListings;
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

  tableActionReset() {
    this.userService.refreshUser()
    this.selection.clear()
  }

  userUpdateSelected() {
    const selectedCount = this.selection.selected.length;
    if (selectedCount !== 1) {
      this.snackBar.open('Please select exactly one listing to update at a time.', 'OK',
        {duration: 4500, verticalPosition: 'top', horizontalPosition: 'center', panelClass: 'my-snackbar'})
        .onAction().subscribe(() => this.snackBar.dismiss());
      return;
    }
    const updateListing = this.selection.selected[0];
    this.router.navigate(['/update-listing'], {
      state: { draft: updateListing }
    });
    // if(!environment.production) {
    //   console.log("HERE IS THE LISTING DATA TO BE UPDATED: ", updateListing);
    // };
  }

  userDeleteListings() {
    const selectedCount = this.selection.selected.length;
    for (let i = 0; i < this.selection.selected.length; i++) {
      this.userListingService.deleteListing(this.selection.selected[i].groupId).subscribe({
        next: response => {
          if(!environment.production) {
            console.log(response.listingTitle)
          }
          this.tableActionReset()
          this.snackBar.open(`You successfully deleted [${selectedCount}] listing(s).`, 'OK', {
            duration: 6000,
            verticalPosition: 'top',
            horizontalPosition: 'center',
            panelClass: ['my-snackbar']
          });
        },
        error: err => {
          alert(`There was an error deleting this listing: ${err.message}`);
        }
      });
    }
  }

  userDeleteSingle(row : GroupListingViewModel) {
    this.userListingService.deleteListing(row.groupId).subscribe({
      next: response => {
        this.tableActionReset();

        const title = row.listingTitle.length > 30
          ? row.listingTitle.slice(0,30) + '...' : row.listingTitle;

        this.snackBar.open(`You successfully deleted "${title}".`, 'OK', {
          duration: 6000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['mobile-snackbar']});
      },
      error: err => {
        alert(`There was an error deleting this listing: ${err.message}`);
      }
    });
  }

  openConfirmDelete(rowOrRows: GroupListingViewModel | GroupListingViewModel[]): void {
     const rows = Array.isArray(rowOrRows) ? rowOrRows : [rowOrRows];
      const dialogRef = this.dialog.open(ConfirmDeleteComponent, {
        data: {
          rows
        }
      });

      dialogRef.afterClosed().subscribe(result => {
        if(result && (rows.length > 1)) {
          this.userDeleteListings();
        }
        else if(result && (rows.length == 1)) {
          const tempRow: GroupListingViewModel = rows[0];
          this.userDeleteSingle(tempRow);
        }
      });
  }

  //on-row-click instructions for groupListing modal popup
  onRowClick(tempListing: GroupListingViewModel) {
    this.listingForModal.emit(tempListing);
    // if(!environment.production) {
    //   console.log("HERE IS THE LISTING DATA: ", tempListing);
    // }
  }

  isMobile$ = this.breakpointObserver
    .observe('(max-width: 499px)')
    .pipe(map(result => result.matches),
      shareReplay());

  mobileUserUpdateSingle(row : GroupListingViewModel) {
    this.router.navigate(['/update-listing'], {
      state: { draft: row }
    })
  }

}
