import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatTableDataSource, MatTableModule} from "@angular/material/table";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {SelectionModel} from "@angular/cdk/collections";
import {AuthService} from "../../services/auth/auth-services/auth.service";
import {DatePipe} from "@angular/common";
import {MatButton} from "@angular/material/button";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-user-acct-listings-table',
  standalone: true,
  templateUrl: './user-acct-listings-table.component.html',
  styleUrl: './user-acct-listings-table.component.css',
  imports: [MatTableModule, MatCheckboxModule, DatePipe]
})
export class UserAcctListingsTableComponent implements OnChanges {
  @Input() userListings: GroupListingViewModel[] = [];

  displayedColumns = [ 'select', 'title', 'status', 'category', 'pvp', 'system', 'roles', 'updated' ]
  dataSource = new MatTableDataSource(this.userListings);
  selection = new SelectionModel<GroupListingViewModel>(true, []);

  constructor(private auth: AuthService) {}

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
}
