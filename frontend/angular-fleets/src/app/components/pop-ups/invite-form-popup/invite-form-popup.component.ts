import {Component, Inject} from '@angular/core';
import {FormControl} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {
  RoleClassSummaryViewModel
} from "../../../models/group-management-models/nested-models/role-class-summary-view-model";
import {rosterClasses} from "../../../services/api-services/group-membership-api/group-membership-api.service";
import {SendGroupInviteRequest} from "../../../models/group-management-models/request-models/send-group-invite-request";

export type GroupInviteFormShape = {

}

@Component({
  selector: 'app-invite-request-popup',
  standalone: false,
  templateUrl: './invite-form-popup.component.html',
  styleUrl: './invite-form-popup.component.css'
})

export class InviteFormPopupComponent {
  rosterClassCtrl: FormControl<string> = new FormControl<string>('ACTIVE', {nonNullable: true});
  msgInputCtrl: FormControl<string | null> = new FormControl<string | null>(null);
  roleCtrl: FormControl<RoleClassSummaryViewModel | null> = new FormControl<RoleClassSummaryViewModel | null>(null);

  rosterVals: Map<string, string> = rosterClasses;

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      listingTitle: string,
      listingId: number,
      inviteDirection: string,
      groupRoles: RoleClassSummaryViewModel[],
    },
    private dialogRef: MatDialogRef<InviteFormPopupComponent>,
  ) {}

  /*TODO add rules for invite offer */
  onConfirm(): void {
    if(this.data.inviteDirection === 'REQUEST') {
      const invRequest = new SendGroupInviteRequest(this.data.listingId, this.rosterClassCtrl.value, this.msgInputCtrl.value)
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
