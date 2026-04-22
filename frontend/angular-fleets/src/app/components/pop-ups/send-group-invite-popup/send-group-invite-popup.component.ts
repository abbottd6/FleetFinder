import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
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
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {
  UserMonikerSummaryViewModel
} from "../../../models/group-management-models/nested-models/user-moniker-summary-view-model";
import {Subject} from "rxjs";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {
  AuthorizedMemberGroupSelectDropdownComponent
} from "./authorized-member-group-select-dropdown/authorized-member-group-select-dropdown.component";
import {NgIf} from "@angular/common";
import {
  AbstractStringDropdownComponent
} from "../../dropdowns/abstract-string-string-map-dropdown/abstract-string-dropdown.component";
import {rosterClasses} from "../../../services/api-services/group-membership-api/group-membership-api.service";
import {
  GenericMediumInputFieldComponent
} from "../../input-fields/generic-medium-input-field/generic-medium-input-field.component";
import {SendGroupInviteOffer} from "../../../models/group-management-models/request-models/send-group-invite-offer";
import {
  RoleClassSummaryViewModel
} from "../../../models/group-management-models/nested-models/role-class-summary-view-model";

export type InviteOfferFormShape = {
  listingCtrl: FormControl<GroupListingViewModel | null>;
  recipientCtrl: FormControl<UserMonikerSummaryViewModel | null>;
  rosterClassCtrl: FormControl<string | null>;
  roleSummaryCtrl: FormControl<RoleClassSummaryViewModel | null>;
  messageCtrl: FormControl<string | null>;
  expiryCtrl: FormControl<Date | null>;
}

@Component({
  selector: 'app-send-group-invite-popup',
  imports: [
    SearchInputAutoCompleteComponent,
    MatDialogContent,
    MatDialogTitle,
    MatDialogActions,
    AuthorizedMemberGroupSelectDropdownComponent,
    NgIf,
    AbstractStringDropdownComponent,
    GenericMediumInputFieldComponent
  ],
  templateUrl: './send-group-invite-popup.component.html',
  styleUrl: './send-group-invite-popup.component.css'
})
export class SendGroupInvitePopupComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  protected inviteForm!: FormGroup<InviteOfferFormShape>;

  protected showGroupSelect: boolean = false;

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      listing: GroupListingViewModel | null,
      recipientSummary: UserMonikerSummaryViewModel | null,
    },
    private dialogRef: MatDialogRef<SendGroupInvitePopupComponent>,
    private formBuilder: FormBuilder
  ){}

  ngOnInit() {
    this.inviteForm = this.buildForm();

    if(this.data?.listing) {
      this.showGroupSelect = false;
      this.inviteForm.controls.listingCtrl.setValue(this.data.listing);
    } else {
      this.showGroupSelect = true;
    }
  }

  onCancel() {
    this.dialogRef.close(null);
  }

  onConfirm() {
    if(this.inviteForm.invalid) {
      this.inviteForm.markAllAsTouched();
      return;
    }

    const invite = new SendGroupInviteOffer(this.inviteForm)

    this.dialogRef.close(invite);
  }

  buildForm(): FormGroup<InviteOfferFormShape> {
    return this.formBuilder.group<InviteOfferFormShape>({
      listingCtrl: new FormControl<GroupListingViewModel | null>(null, [Validators.required]),
      recipientCtrl: new FormControl<UserMonikerSummaryViewModel | null>(null, [Validators.required]),
      rosterClassCtrl: new FormControl<string | null>(null, [Validators.required]),
      roleSummaryCtrl: new FormControl<RoleClassSummaryViewModel | null>(null),
      messageCtrl: new FormControl<string | null>(null),
      expiryCtrl: new FormControl<Date | null>(null),
    })
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly rosterVals = rosterClasses;
}
