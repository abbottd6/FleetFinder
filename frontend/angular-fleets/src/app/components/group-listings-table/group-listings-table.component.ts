import {Component, Input, OnInit} from '@angular/core';
import {GroupListingService} from "../../services/group-listing.service";
import {GroupListing} from "../../common/group-listing";

@Component({
  selector: 'app-group-listings-table',
  templateUrl: './group-listings-table.component.html',
  styleUrl: './group-listings-table.component.css'
})
export class GroupListingsTableComponent implements OnInit {

  @Input() public groupListings: GroupListing[] = [];

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
