import {Component, Input, OnInit} from '@angular/core';
import {GroupListingFetchService} from "../../services/group-listing-services/group-listing-fetch.service";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {environment} from "../../../environments/environment";

@Component({
    selector: 'app-group-listings-table',
    templateUrl: './group-listings.component.html',
    styleUrl: './group-listings.component.css',
    standalone: false
})
export class GroupListingsComponent implements OnInit {
  @Input() public groupListings: GroupListingViewModel[] = []
  selectedListing: GroupListingViewModel | null = null;
  isModalVisible: boolean = false;

  constructor(private groupListingService: GroupListingFetchService) {
  }

  //on-row-click instructions for groupListing modal popup
  onRowClick(tempListing: GroupListingViewModel) {
    this.selectedListing = tempListing;
    if(!environment.production) {
      console.log("Logging selected listing ID: ", this.selectedListing.groupId);
    }
    this.isModalVisible = true;
    if(!environment.production) {
      console.log("Parent modal visibility: ", this.isModalVisible);
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

  loadGroupListings() {
    this.groupListingService.getGroupListings().subscribe({
      next: (data: GroupListingViewModel[]) => {
        if(!environment.production) {
          console.log('Data received in component:', data);
        }
        this.groupListings = data;
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
