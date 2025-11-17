import {AfterViewInit, Component, inject, Input, OnInit, ViewChild} from '@angular/core';
import {GroupListingFetchService} from "../../services/group-listing-services/group-listing-fetch.service";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {environment} from "../../../environments/environment";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatMenuTrigger} from "@angular/material/menu";
import {GroupListingModalComponent} from "../group-listing-modal/group-listing-modal.component";
import {TooltipPosition} from "@angular/material/tooltip";
import {MatSort, MatSortHeader, Sort} from "@angular/material/sort";
import {MatTableDataSource} from "@angular/material/table";
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {MatPaginator} from "@angular/material/paginator";

@Component({
    selector: 'app-group-listings-table',
    templateUrl: './group-listings.component.html',
    styleUrl: './group-listings.component.css',
    standalone: false
})

export class GroupListingsComponent implements OnInit, AfterViewInit{
  @ViewChild(MatSort) sort!: MatSort;

  private CLICKED_KEY = 'ff_user_clicked_listings';
  private _liveAnnouncer = inject(LiveAnnouncer)

  positionOptions: TooltipPosition[] = ['after', 'before', 'above', 'below', 'left', 'right'];
  selectedListing: GroupListingViewModel | null = null;
  isModalVisible: boolean = false;
  clickedRows = new Set<number>();

  /* TO DO: set up bookmarks and change this */
  userBookmarks: GroupListingViewModel[] = [];

  displayedColumns = ['options', 'title', 'status', 'category', 'pvp', 'system', 'roles', 'updated'];
  dataSource = new MatTableDataSource<GroupListingViewModel>();

  constructor(private groupListingService: GroupListingFetchService, private snackBar: MatSnackBar) {}

  //on-row-click instructions for groupListing modal popup
  onRowClick(tempListing: GroupListingViewModel) {
    this.selectedListing = tempListing;
    // if(!environment.production) {
    //   console.log("HERE IS THE LISTING DATA: ", tempListing);
    // }
    // if(!environment.production) {
    //   console.log("Logging selected listing ID: ", this.selectedListing.groupId);
    // }
    this.isModalVisible = true;
    // if(!environment.production) {
    //   console.log("Parent modal visibility: ", this.isModalVisible);
    // }
    // console.log("CLICKED ROWS: ", this.clickedRows)
  }

  saveRowClick(row: number) {
    this.clickedRows.add(row);
    const arr = Array.from(this.clickedRows);
    localStorage.setItem(this.CLICKED_KEY, JSON.stringify(arr));
    // console.log("clickedRows saved: ", this.clickedRows)
  }

  private loadClickedListings() {
    const clickedListings = localStorage.getItem(this.CLICKED_KEY);
    if (!clickedListings) return;

    try {
      const arr: number[] = JSON.parse(clickedListings);
      this.clickedRows = new Set(arr);
    } catch {
      //
    }
  }

  //on close instructions for groupListing modal popup
  onModalClose() {
    if(!environment.production) {
      console.log("Modal closed");
    }
    this.isModalVisible = false;
    this.selectedListing = null;
  }

  ngOnInit(): void {
    this.loadGroupListings();

  }

  ngAfterViewInit() {
    this.loadClickedListings();
    this.dataSource.sort = this.sort;
  }

  isRowClicked(row: GroupListingViewModel): boolean {
    return this.clickedRows.has(row.groupId);
  }

  announceSortChange(sortState: Sort) {
    if (sortState.direction) {
      this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
    } else {
      this._liveAnnouncer.announce('Sorting cleared');
    }
  }

  loadGroupListings() {
    this.groupListingService.getGroupListings().subscribe({
      next: (data: GroupListingViewModel[]) => {
        // if(!environment.production) {
        //   console.log('Data received in component:', data);
        // }
        this.dataSource.data = data;
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
}
