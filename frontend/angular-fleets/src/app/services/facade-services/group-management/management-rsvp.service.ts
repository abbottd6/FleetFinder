import {DestroyRef, inject, Injectable } from '@angular/core';
import {
  MasterRsvpViewModel
} from "../../../models/group-management-models/view-models/management-rsvp/master-rsvp-view-model";
import {ManagementRsvpApiService} from "../../api-services/group-management/management-rsvp-api.service";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import {
  MasterRsvpResponseWrapper
} from "../../../models/group-management-models/view-models/management-rsvp/master-rsvp-response-wrapper";
import {MatDialog} from "@angular/material/dialog";
import {
  GroupCompSubgroupViewModel
} from "../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";
import {
  ScheduleRsvpPopupComponent
} from "../../../components/group-management-page/subgroup-management/schedule-rsvp-popup/schedule-rsvp-popup.component";
import {GroupListingFetchService} from "../../api-services/group-listings-fetch-api/group-listing-fetch.service";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {GroupManagementInteractService} from "./group-management-interact.service";

@Injectable({
  providedIn: 'root'
})
export class ManagementRsvpService {
  private destroyRef = inject(DestroyRef);

  private activeMastersMap: Map<number | null, MasterRsvpViewModel> = new Map();

  public hasGlobalMaster: boolean = false;

  constructor(private mgmtRsvpApi: ManagementRsvpApiService,
              private listingApi: GroupListingFetchService,
              private managementInteract: GroupManagementInteractService,
              private dialog: MatDialog) {}

  getRsvpActiveMasters(listingId: number) {
    this.mgmtRsvpApi.getRsvpActiveMastersList(listingId).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: ((responseWrapper: MasterRsvpResponseWrapper) => {
          this.hasGlobalMaster = responseWrapper.hasGlobalMaster;

          this.activeMastersMap = new Map(
            responseWrapper.mastersList.map(m => [m.subgroupId, m])
          );
        })
      })
  }

  openScheduleGlobalRsvpPopup(listing: GroupListingViewModel) {
    const dialogRef = this.dialog.open(ScheduleRsvpPopupComponent, {
      minWidth: '700px',
      minHeight: '500px',
      data: {
        isGlobalRsvp: true,
        subgroups: null,
        listing: listing
      }
    })
  }

  openScheduleGranularRsvpPopup(subgroup: GroupCompSubgroupViewModel){
    const listing = this.managementInteract.groupListing;

    const dialogRef = this.dialog.open(ScheduleRsvpPopupComponent, {
      minWidth: '700px',
      minHeight: '500px',
      data: {
        isGlobalRsvp: false,
        subgroup: subgroup,
        listing: listing
      }
    })
  }
}

