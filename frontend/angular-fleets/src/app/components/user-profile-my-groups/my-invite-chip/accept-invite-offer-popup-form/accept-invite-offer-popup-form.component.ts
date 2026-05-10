import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {GroupListingViewModel} from "../../../../models/group-listing/group-listing-view-model";
import {NgIf} from "@angular/common";
import {toTitleCase} from "../../../../utils/global-functions";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {
  GenericSmallInputFieldComponent
} from "../../../input-fields/generic-small-input-field/generic-small-input-field.component";
import {MatCheckbox} from "@angular/material/checkbox";

export interface UserAcceptInviteOfferFormData {
  inGameUsername: string,
  hasMic: boolean,
  hasHeadset: boolean
}

@Component({
  selector: 'app-accept-invite-offer-popup-form',
  imports: [
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle,
    NgIf,
    GenericSmallInputFieldComponent,
    ReactiveFormsModule,
    MatCheckbox
  ],
  templateUrl: './accept-invite-offer-popup-form.component.html',
  styleUrl: './accept-invite-offer-popup-form.component.css'
})
export class AcceptInviteOfferPopupFormComponent {

    protected groupCommsStatus!: string;
    protected inGameUsernameCtrl: FormControl<string> = new FormControl<string>('', { nonNullable: true })
    protected micCtrl: FormControl<boolean> = new FormControl<boolean>(false, { nonNullable: true });
    protected headsetCtrl: FormControl<boolean> = new FormControl<boolean>(false, { nonNullable: true })

    constructor(
      @Inject(MAT_DIALOG_DATA)
      public data: {
        listing: GroupListingViewModel,
        inGameUsername: string,
      },
      private dialogRef: MatDialogRef<AcceptInviteOfferPopupFormComponent>,
    ){

      this.groupCommsStatus = data.listing.commsOption;
      this.inGameUsernameCtrl.setValue(data.inGameUsername);
    }

  onConfirm(): void {
    const formData: UserAcceptInviteOfferFormData = {
      inGameUsername: this.inGameUsernameCtrl.value,
      hasMic: this.micCtrl.value,
      hasHeadset: this.headsetCtrl.value
    };

    this.dialogRef.close(formData);
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  protected readonly toTitleCase = toTitleCase;
}
