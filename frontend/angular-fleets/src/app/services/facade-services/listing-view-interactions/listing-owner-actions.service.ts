import { Injectable } from '@angular/core';
import {environment} from "../../../../environments/environment";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {ConfirmDeleteComponent} from "../../../components/pop-ups/confirm-delete/confirm-delete.component";
import {UserListingManagementService} from "../../user-services/user-listing-management.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {UserService} from "../../user-services/user.service";

@Injectable({
  providedIn: 'root'
})
export class ListingOwnerActionsService {

  constructor(private userListingService: UserListingManagementService,
              private userService: UserService,
              private snackBar: MatSnackBar,
              private dialog: MatDialog,
              private router: Router) { }

  userUpdateSelected(selection: GroupListingViewModel) {
    console.log(selection);

    this.router.navigate(['/update-listing'], {
      state: { draft: selection }
    });
  }

  userDeleteListings(selection: GroupListingViewModel[]){
    const selectedCount = selection.length;
    for (let i = 0; i < selection.length; i++) {
      this.userListingService.deleteListing(selection[i].groupId)
        .subscribe({
          next: response => {
            this.userService.refreshUser();

            if(!environment.production) {
              console.log(response.listingTitle)
            }
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

        this.userService.refreshUser();

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
        this.userDeleteListings(rows);
      }
      else if(result && (rows.length == 1)) {
        const tempRow: GroupListingViewModel = rows[0];
        this.userDeleteSingle(tempRow);
      }
    });
  }
}
