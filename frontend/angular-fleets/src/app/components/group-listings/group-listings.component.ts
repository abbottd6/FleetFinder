import {Component, Input, OnInit} from '@angular/core';
import {GroupListingService} from "../../services/group-listing.service";
import {GroupListing} from "../../common/group-listing";

@Component({
    selector: 'app-group-listings-table',
    templateUrl: './group-listings.component.html',
    styleUrl: './group-listings.component.css',
    standalone: false
})
export class GroupListingsComponent implements OnInit {

  @Input() public groupListings: GroupListing[] = [];

  constructor(private groupListingService: GroupListingService) {
  }

  ngOnInit(): void {
    this.loadGroupListings();
  }

  loadGroupListings() {
    this.groupListingService.getGroupListings().subscribe({
      next: (data: GroupListing[]) => {
        console.log('Data received in component:', data);
        this.groupListings = data;
      },
      error: (error) => {
        console.log('Error fetching group listings from component:', error);
      },
      complete: () => {
        console.log('Group listings fetching completed.');
      }
    });
  }
}
