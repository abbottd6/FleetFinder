import {Component, Inject} from '@angular/core';
import {FormControl, ReactiveFormsModule} from "@angular/forms";
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
import {
  GenericSmallInputFieldComponent
} from "../../input-fields/generic-small-input-field/generic-small-input-field.component";
import {MatCheckbox} from "@angular/material/checkbox";

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
    GenericSmallInputFieldComponent,
    MatCheckbox,
    ReactiveFormsModule,
  ],
  styleUrl: './invite-form-popup.component.css'
})

//TODO add template form fields for mic and headset
export class InviteFormPopupComponent {
  inGameUsernameCtrl: FormControl<string> = new FormControl<string>('', {nonNullable: true});
  rosterClassCtrl: FormControl<string> = new FormControl<string>('', {nonNullable: true});
  msgInputCtrl: FormControl<string | null> = new FormControl<string | null>(null);
  roleCtrl: FormControl<RoleClassSummaryViewModel | null> = new FormControl<RoleClassSummaryViewModel | null>(null);
  micCtrl: FormControl<boolean> = new FormControl<boolean>(false, {nonNullable: true});
  headsetCtrl: FormControl<boolean> = new FormControl<boolean>(false, {nonNullable: true});

  showRosterFullMessage: boolean = false;

  rosterVals: string[] = rosterClasses;

  groupPositions!: string[];

  title!: string;

  //TODO set up hasMic and hasComms fields

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      listing: GroupListingViewModel,
      inGameUsername: string,
      inviteDirection: string,
      groupPositions: RoleClassSummaryViewModel[],
    },
    private dialogRef: MatDialogRef<InviteFormPopupComponent>,
  ) {

    this.title = "Request to Join This Group";

    if(data.inGameUsername != null) {
      this.inGameUsernameCtrl.setValue(data.inGameUsername);
    }

    if(data.listing.currentPartySize >= data.listing.desiredPartySize) {
      this.showRosterFullMessage = true;
      this.rosterClassCtrl.setValue('Waitlist');
    } else {
      this.rosterClassCtrl.setValue('Active')
    }

    if(data.groupPositions) {
      data.groupPositions.forEach(r => this.groupPositions.push(r.roleTitle))
    }
  }

  onConfirm(): void {
    const invRequest = new SendGroupInviteRequest(this.data.listing.groupId, this.inGameUsernameCtrl.value,
        this.rosterClassCtrl.value.toUpperCase(), this.msgInputCtrl.value, this.micCtrl.value, this.headsetCtrl.value);

    this.dialogRef.close(invRequest);
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }
}
