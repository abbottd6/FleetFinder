import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {
  GroupMembershipViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-membership-view-model";

@Component({
  selector: 'app-send-group-invite-popup',
  imports: [],
  templateUrl: './send-group-invite-popup.component.html',
  styleUrl: './send-group-invite-popup.component.css'
})
export class SendGroupInvitePopupComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      sessionMember: GroupMembershipViewModel,
    },
    private dialogRef: MatDialogRef<SendGroupInvitePopupComponent>,
  ){}
}
