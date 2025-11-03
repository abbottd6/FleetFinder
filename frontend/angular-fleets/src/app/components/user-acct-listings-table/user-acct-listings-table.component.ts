import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatSnackBar, MatSnackBarModule} from "@angular/material/snack-bar";
import {MatTableDataSource, MatTableModule} from "@angular/material/table";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {SelectionModel} from "@angular/cdk/collections";
import {AuthService} from "../../services/auth/auth-services/auth.service";
import {DatePipe} from "@angular/common";
import {Router, RouterLink} from "@angular/router";
import {UserListingService} from "../../services/group-listing-services/user-listing.service";
import {UserService} from "../../services/user-services/user.service";
import {environment} from "../../../environments/environment";
import {MatButtonModule} from "@angular/material/button";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

@Component({
  selector: 'app-user-acct-listings-table',
  standalone: true,
  templateUrl: './user-acct-listings-table.component.html',
  styleUrl: './user-acct-listings-table.component.css',
  imports: [MatTableModule, MatCheckboxModule, DatePipe],
})
export class UserAcctListingsTableComponent implements OnChanges {
  @Input() userListings: GroupListingViewModel[] = [];

  displayedColumns = [ 'select', 'title', 'status', 'category', 'pvp', 'system', 'roles', 'updated' ]
  dataSource = new MatTableDataSource(this.userListings);
  selection = new SelectionModel<GroupListingViewModel>(true, []);

  constructor(private auth: AuthService, private userListingService: UserListingService,
              private router: Router, private userService: UserService, private snackBar: MatSnackBar) {}

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

    this.router.navigateByUrl("/app-user")
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
  }

  userDeleteListing() {
    const selectedCount = this.selection.selected.length;
    for (let i = 0; i < this.selection.selected.length; i++) {
      this.userListingService.deleteListing(this.selection.selected[i].groupId).subscribe({
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
}
