import {Component, Inject} from '@angular/core';
import {FormControl} from "@angular/forms";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {
  RoleClassSummaryViewModel
} from "../../../models/group-management-models/nested-models/role-class-summary-view-model";
import {rosterClasses} from "../../../services/api-services/group-membership-api/group-membership-api.service";
import {SendGroupInviteRequest} from "../../../models/group-management-models/request-models/send-group-invite-request";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {
  GenericMediumInputFieldComponent
} from "../../input-fields/generic-medium-input-field/generic-medium-input-field.component";
import {
  AbstractStringDropdownComponent
} from "../../dropdowns/abstract-string-string-map-dropdown/abstract-string-dropdown.component";
import {NgIf, SlicePipe} from "@angular/common";

@Component({
  selector: 'app-invite-request-popup',
  standalone: true,
  templateUrl: './invite-form-popup.component.html',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    GenericMediumInputFieldComponent,
    AbstractStringDropdownComponent,
    MatDialogActions,
    NgIf,
    SlicePipe,
  ],
  styleUrl: './invite-form-popup.component.css'
})

export class InviteFormPopupComponent {
  rosterClassCtrl: FormControl<string> = new FormControl<string>('Active', {nonNullable: true});
  msgInputCtrl: FormControl<string | null> = new FormControl<string | null>(null);
  roleCtrl: FormControl<RoleClassSummaryViewModel | null> = new FormControl<RoleClassSummaryViewModel | null>(null);

  rosterVals: string[] = rosterClasses;

  groupRoles!: string[];

  title!: string;

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      listing: GroupListingViewModel,
      inviteDirection: string,
      groupRoles: RoleClassSummaryViewModel[],
    },
    private dialogRef: MatDialogRef<InviteFormPopupComponent>,
  ) {

    if(data.inviteDirection === 'REQUEST') {
      this.title = "Request to Join This Group"
    } else {
      this.title = "Send Group Invite"
    }

    if(data.groupRoles) {
      data.groupRoles.forEach(r => this.groupRoles.push(r.roleTitle))
    }
  }

  /*TODO add rules for invite offer */
  onConfirm(): void {
    if(this.data.inviteDirection === 'REQUEST') {
      const invRequest = new SendGroupInviteRequest(this.data.listing.groupId,
        this.rosterClassCtrl.value.toUpperCase(), this.msgInputCtrl.value)
      this.dialogRef.close(invRequest);
    } else if(this.data.inviteDirection === 'OFFER') {
      console.log("need to set up 'sendGroupInviteOffer' model and configure it to be used in the popup.")
      this.dialogRef.close(null);
    } else {
      this.dialogRef.close(null);
    }
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }
}
