import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors} from "@angular/forms";
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
import {combineLatest, debounceTime, merge, Subject, takeUntil} from "rxjs";
import {MatError} from "@angular/material/input";

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
    MatError,
  ],
  styleUrl: './invite-form-popup.component.css'
})

//TODO add template form fields for mic and headset
export class InviteFormPopupComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  inGameUsernameCtrl: FormControl<string> = new FormControl<string>('', {nonNullable: true});
  rosterClassCtrl: FormControl<string> = new FormControl<string>('', {nonNullable: true});
  msgInputCtrl: FormControl<string | null> = new FormControl<string | null>(null);
  roleCtrl: FormControl<RoleClassSummaryViewModel | null> = new FormControl<RoleClassSummaryViewModel | null>(null);

  micCtrl: FormControl<boolean> = new FormControl<boolean>(false, {nonNullable: true});
  headsetCtrl: FormControl<boolean> = new FormControl<boolean>(false, {nonNullable: true});
  noCommsCtrl: FormControl<boolean> = new FormControl<boolean>(false, {nonNullable: true});
  commsFormGroup: FormGroup = new FormGroup({
    mic: this.micCtrl,
    headset: this.headsetCtrl,
    noComms: this.noCommsCtrl
  }, { validators: atLeastOneCommsOptionSelected });

  showRosterFullMessage: boolean = false;

  rosterVals: string[] = rosterClasses;

  groupPositions!: string[];

  title!: string;

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

  ngOnInit() {
    this.noCommsCtrl.valueChanges.pipe(
      takeUntil(this.destroy$))
      .subscribe(value => {
        if(value) {
          this.headsetCtrl.setValue(false, { emitEvent: false });
          this.micCtrl.setValue(false, { emitEvent: false });
          this.commsFormGroup.markAllAsTouched();
          this.commsFormGroup.markAsDirty();
        }
      })

    merge(
      this.headsetCtrl.valueChanges,
      this.micCtrl.valueChanges
    ).pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if(this.headsetCtrl.value || this.micCtrl.value) {
          this.noCommsCtrl.setValue(false, { emitEvent: false });
          this.commsFormGroup.markAllAsTouched();
          this.commsFormGroup.markAsDirty();
        }
      })
  }

  onConfirm(): void {
    if(atLeastOneCommsOptionSelected(this.commsFormGroup)) {
      this.commsFormGroup.markAllAsTouched();
      this.commsFormGroup.markAsDirty();
      return;
    }

    const invRequest = new SendGroupInviteRequest(this.data.listing.groupId, this.inGameUsernameCtrl.value,
        this.rosterClassCtrl.value.toUpperCase(), this.msgInputCtrl.value, this.micCtrl.value, this.headsetCtrl.value);

    this.dialogRef.close(invRequest);
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

function atLeastOneCommsOptionSelected(group: AbstractControl): ValidationErrors | null {
  const { mic, headset, noComms } = group.value;
  return (mic || headset || noComms) ? null : { noSelection: true };
}
