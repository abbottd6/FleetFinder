import { Component } from '@angular/core';
import {GroupListingService} from "../../services/group-listing.service";
import {GroupListing} from "../../common/group-listing";

@Component({
  selector: 'app-group-listings',
  templateUrl: './group-listings.component.html',
  styleUrl: './group-listings.component.css'
})
export class GroupListingsComponent {

  groupListings: GroupListing[] = [];

  constructor(private groupListingService: GroupListingService) { }

  ngOnInit(): void {
    this.loadGroupListings();
  }

  loadGroupListings() {
    this.groupListingService.getGroupListings().subscribe(
      data => {
        this.groupListings = data;
      }
    )
  }
}
