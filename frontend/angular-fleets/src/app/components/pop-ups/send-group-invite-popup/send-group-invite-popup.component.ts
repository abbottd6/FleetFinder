import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {
  GroupMembershipViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-membership-view-model";
import {
  SearchInputAutoCompleteComponent
} from "../../input-fields/search-input-auto-complete/search-input-auto-complete.component";
import {PublicUser} from "../../../models/public-user/public-user";
import {FormControl} from "@angular/forms";
import {
  UserMonikerSummaryViewModel
} from "../../../models/group-management-models/nested-models/user-moniker-summary-view-model";

@Component({
  selector: 'app-send-group-invite-popup',
  imports: [
    SearchInputAutoCompleteComponent,
    MatDialogContent,
    MatDialogTitle,
    MatDialogActions
  ],
  templateUrl: './send-group-invite-popup.component.html',
  styleUrl: './send-group-invite-popup.component.css'
})
export class SendGroupInvitePopupComponent {

  recipientCtrl: FormControl<UserMonikerSummaryViewModel | null> = new FormControl<UserMonikerSummaryViewModel | null>(null);

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      sessionMember: GroupMembershipViewModel,
    },
    private dialogRef: MatDialogRef<SendGroupInvitePopupComponent>,
  ){}

  onCancel() {
    this.dialogRef.close(null);
  }

  onConfirm() {
    this.dialogRef.close(null);
  }
}
