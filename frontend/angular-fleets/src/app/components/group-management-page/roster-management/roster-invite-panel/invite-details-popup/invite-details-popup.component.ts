import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {
  GroupManagementInviteViewModel
} from "../../../../../models/group-management-models/view-models/group-membership/group-management-invite-view-model";
import {ReactiveFormsModule} from "@angular/forms";
import {DatePipe, NgIf, TitleCasePipe} from "@angular/common";
import {
  UserMonikerSummaryViewModel
} from "../../../../../models/group-management-models/nested-models/user-moniker-summary-view-model";
import {MatIcon} from "@angular/material/icon";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {InviteActions} from "../invite-chip/invite-chip.component";

@Component({
  selector: 'app-invite-details-popup',
  templateUrl: './invite-details-popup.component.html',
  imports: [
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle,
    ReactiveFormsModule,
    TitleCasePipe,
    DatePipe,
    MatIcon,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger,
    NgIf
  ],
  styleUrl: './invite-details-popup.component.css'
})
export class InviteDetailsPopupComponent {

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data:{
      invite: GroupManagementInviteViewModel,
      targetMember: UserMonikerSummaryViewModel
    },
    private dialogRef: MatDialogRef<InviteDetailsPopupComponent>,
  ) {}

  onConfirmAction(action: InviteActions) {
    this.dialogRef.close(action);
  }

  onCancel() {
    this.dialogRef.close(null);
  }

  protected readonly InviteActions = InviteActions;
}
