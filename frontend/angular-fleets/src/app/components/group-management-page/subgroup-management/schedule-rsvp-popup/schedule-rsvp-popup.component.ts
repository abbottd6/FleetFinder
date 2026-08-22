import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {
  GroupCompSubgroupViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {ManagementRsvpService} from "../../../../services/facade-services/group-management/management-rsvp.service";
import {Subject} from "rxjs";
import {GroupListingViewModel} from "../../../../models/group-listing/group-listing-view-model";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-schedule-rsvp-popup',
  imports: [
    MatDialogContent,
    MatDialogTitle,
    MatDialogActions,
    MatIcon
  ],
  templateUrl: './schedule-rsvp-popup.component.html',
  styleUrl: './schedule-rsvp-popup.component.css'
})
export class ScheduleRsvpPopupComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  popupTitle: string = 'Schedule Subgroup-Level RSVP'

  showMoreInfo: boolean = false;
  infoExpanding: boolean = false;

  constructor(@Inject(MAT_DIALOG_DATA)
  public data: {
    isGlobalRsvp: boolean,
    subgroup: GroupCompSubgroupViewModel,
    listing: GroupListingViewModel
  }, private dialogRef: MatDialogRef<ScheduleRsvpPopupComponent>,
     private mgmtRsvpService: ManagementRsvpService
  ){}

  ngOnInit() {

    if(this.data.isGlobalRsvp) {
      this.popupTitle = 'Schedule Global RSVP'
    }
  }

  toggleShowMoreInfo() {
    this.infoExpanding = true;
    this.showMoreInfo = !this.showMoreInfo;

    setTimeout(() => this.infoExpanding = false, 200);
  }

  onConfirm() {
    return this.dialogRef.close(null);
  }

  onCancel() {
    return this.dialogRef.close(null);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
