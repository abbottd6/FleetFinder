import {Component, Input, OnInit} from '@angular/core';
import {GroupListingService} from "../../services/group-listing.service";
import {GroupListing} from "../../common/group-listing";

@Component({
  selector: 'app-group-listings-table',
  templateUrl: './group-listings.component.html',
  styleUrl: './group-listings.component.css'
})
export class GroupListingsComponent implements OnInit {

  @Input() public groupListings: GroupListing[] = [];

  constructor(private groupListingService: GroupListingService) { }

  ngOnInit(): void {
    this.loadGroupListings();
  }

  loadGroupListings() {
    this.groupListingService.getGroupListings().subscribe(
        (data: GroupListing[]) => {
        this.groupListings = data;
      }
    )
  }
}
