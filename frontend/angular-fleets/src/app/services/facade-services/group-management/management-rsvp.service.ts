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

@Injectable({
  providedIn: 'root'
})
export class ManagementRsvpService {
  private destroyRef = inject(DestroyRef);

  private activeMastersMap: Map<number | null, MasterRsvpViewModel> = new Map();

  public hasGlobalMaster: boolean = false;

  constructor(private mgmtRsvpApi: ManagementRsvpApiService,
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

  openScheduleRsvpPopup()
}

