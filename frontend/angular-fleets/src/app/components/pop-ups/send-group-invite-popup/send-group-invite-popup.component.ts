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
import {FormControl} from "@angular/forms";
import {
  UserMonikerSummaryViewModel
} from "../../../models/group-management-models/nested-models/user-moniker-summary-view-model";
import {Subject} from "rxjs";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {
  AuthorizedMemberGroupSelectDropdownComponent
} from "./authorized-member-group-select-dropdown/authorized-member-group-select-dropdown.component";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-send-group-invite-popup',
  imports: [
    SearchInputAutoCompleteComponent,
    MatDialogContent,
    MatDialogTitle,
    MatDialogActions,
    AuthorizedMemberGroupSelectDropdownComponent,
    NgIf
  ],
  templateUrl: './send-group-invite-popup.component.html',
  styleUrl: './send-group-invite-popup.component.css'
})
export class SendGroupInvitePopupComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  protected showGroupSelect: boolean = false;

  groupSelectCtrl: FormControl<GroupListingViewModel | null> = new FormControl<GroupListingViewModel | null>(null);
  recipientCtrl: FormControl<UserMonikerSummaryViewModel | null> = new FormControl<UserMonikerSummaryViewModel | null>(null);

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      listing: GroupListingViewModel | null,
      recipientSummary: UserMonikerSummaryViewModel | null,
    },
    private dialogRef: MatDialogRef<SendGroupInvitePopupComponent>,
  ){}

  ngOnInit() {
    if(this.data?.listing) {
      this.showGroupSelect = true;
      this.groupSelectCtrl.setValue(this.data.listing);
    } else {
      this.showGroupSelect = true;
    }
  }

  onCancel() {
    this.dialogRef.close(null);
  }

  onConfirm() {
    this.dialogRef.close(null);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
