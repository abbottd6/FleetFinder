import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {
  GroupMembershipViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-membership-view-model";
import { Subject } from "rxjs";
import {DatePipe, NgIf, SlicePipe, TitleCasePipe} from "@angular/common";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {MatTooltip} from "@angular/material/tooltip";
import {MatIcon} from "@angular/material/icon";
import {
  ListingViewInteractionsService
} from "../../../services/facade-services/listing-view-interactions/listing-view-interactions.service";
import {UiPrefsService} from "../../../services/facade-services/ui-prefs/ui-prefs.service";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-groups-chip',
  standalone: true,
  templateUrl: './groups-chip.component.html',
  imports: [
    DatePipe,
    SlicePipe,
    NgIf,
    TitleCasePipe,
    MatTooltip,
    MatIcon
  ],
  styleUrl: './groups-chip.component.css'
})
export class GroupsChipComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>;

  @Output() emitListing = new EventEmitter<GroupListingViewModel>();
  @Input() membership!: GroupMembershipViewModel;

  protected listingDetails!: GroupListingViewModel;

  constructor(protected listingInteract: ListingViewInteractionsService, private dialog: MatDialog) {}

  ngOnInit() {
    this.listingDetails = this.membership.listing;
  }

  showListingModal() {
    this.emitListing.emit(this.listingDetails);
  }
  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
