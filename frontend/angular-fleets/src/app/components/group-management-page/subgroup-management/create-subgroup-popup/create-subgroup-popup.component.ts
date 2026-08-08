import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subject, takeUntil} from "rxjs";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {GROUP_COMP_ROLE_CATEGORIES} from "../create-or-edit-position-popup/create-or-edit-position-popup.component";
import {
  RoleClassSummaryViewModel
} from "../../../../models/group-management-models/nested-models/role-class-summary-view-model";
import {FormControl, Validators} from "@angular/forms";
import {
  GroupCompositionApiService
} from "../../../../services/api-services/group-management/group-composition-api/group-composition-api.service";
import {
  GroupManagementInteractService
} from "../../../../services/facade-services/group-management/group-management-interact.service";
import {
  GenericSmallInputFieldComponent
} from "../../../input-fields/generic-small-input-field/generic-small-input-field.component";
import {MatIcon} from "@angular/material/icon";
import {
  GenericMediumInputFieldComponent
} from "../../../input-fields/generic-medium-input-field/generic-medium-input-field.component";

@Component({
  selector: 'app-create-subgroup-popup',
  imports: [
    MatDialogContent,
    MatDialogTitle,
    MatDialogActions,
    GenericSmallInputFieldComponent,
    MatIcon,
    GenericMediumInputFieldComponent
  ],
  templateUrl: './create-subgroup-popup.component.html',
  styleUrl: './create-subgroup-popup.component.css'
})
export class CreateSubgroupPopupComponent implements OnInit, OnDestroy{
  private destroy$ = new Subject<void>();

  protected popupTitle!: string;

  protected roleCategories = GROUP_COMP_ROLE_CATEGORIES;
  protected availableClasses: RoleClassSummaryViewModel[] = [];
  protected filteredClasses: RoleClassSummaryViewModel[] = [];

  subgroupLabelCtrl: FormControl<string | null> = new FormControl<string | null>('', {
    nonNullable: true,
    validators: [Validators.required,
                 Validators.minLength(1),
                 Validators.maxLength(32)]
  });
  subgroupNotesCtrl: FormControl<string | null> = new FormControl<string | null>(null, {
    validators: [
      Validators.maxLength(255)]
  });

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      listingId: number,
      parentSubgroupLabel: string | null,
      rootSubgroupId: number | null,
      parentSubgroupId: number | null,
    },
    private dialogRef: MatDialogRef<CreateSubgroupPopupComponent>,
    private compositionApi: GroupCompositionApiService,
    private managementInteract: GroupManagementInteractService
  ){}

  ngOnInit() {
    if(this.data.parentSubgroupLabel) {
      this.popupTitle = 'Add New Nested Subgroup';
    } else {
      this.popupTitle = 'Add New Root Level Subgroup';
    }

    this.compositionApi.getAvailableRoleClassifications(this.data.listingId).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (classifications: RoleClassSummaryViewModel[]) => {
          this.availableClasses = classifications;
        },
        error: () => {
          this.managementInteract.showSnackBarMessage('Failed to fetch reference data.')
        }
      })

  }

  onConfirm() {

  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
