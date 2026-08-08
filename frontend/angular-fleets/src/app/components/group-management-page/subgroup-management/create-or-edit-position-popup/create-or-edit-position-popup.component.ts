import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {
  GroupCompSubgroupViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";
import {Subject, takeUntil} from "rxjs";
import {
  AbstractStringDropdownComponent
} from "../../../dropdowns/abstract-string-string-map-dropdown/abstract-string-dropdown.component";
import {
  GenericMediumInputFieldComponent
} from "../../../input-fields/generic-medium-input-field/generic-medium-input-field.component";
import {NgIf, SlicePipe} from "@angular/common";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {
  RoleClassSummaryViewModel
} from "../../../../models/group-management-models/nested-models/role-class-summary-view-model";
import {
  GroupCompositionApiService
} from "../../../../services/api-services/group-management/group-composition-api/group-composition-api.service";
import {MatError} from "@angular/material/input";
import {NgSelectComponent} from "@ng-select/ng-select";
import {
  GroupCompCrewPositionViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-crew-position-view-model";
import {
  NewOrEditPositionRequest
} from "../../../../models/group-management-models/request-models/new-or-edit-position-request";
import {
  GroupManagementInteractService
} from "../../../../services/facade-services/group-management/group-management-interact.service";

export const GROUP_COMP_ROLE_CATEGORIES: string[] = [
  'Ship Crew',
  'Ground Crew',
  'Support',
  'Command',
];

@Component({
  selector: 'app-create-or-edit-position-popup',
  imports: [
    MatDialogActions,
    AbstractStringDropdownComponent,
    GenericMediumInputFieldComponent,
    MatDialogContent,
    MatDialogTitle,
    NgIf,
    SlicePipe,
    MatError,
    NgSelectComponent,
    ReactiveFormsModule
  ],
  templateUrl: './create-or-edit-position-popup.component.html',
  styleUrl: './create-or-edit-position-popup.component.css'
})
export class CreateOrEditPositionPopupComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  roleCategoryCtrl: FormControl<string | null> = new FormControl<string | null>(null, {nonNullable: true});
  selectedRoleCtrl: FormControl<RoleClassSummaryViewModel | null> = new FormControl<RoleClassSummaryViewModel | null>({
    value: null, disabled: true}, {nonNullable: true});
  positionNoteCtrl: FormControl<string | null> = new FormControl<string | null>(null);

  protected roleCategories = GROUP_COMP_ROLE_CATEGORIES;

  protected availableClasses: RoleClassSummaryViewModel[] = [];
  protected filteredClasses: RoleClassSummaryViewModel[] = [];

  protected contextTitle!: string;
  protected subgroupLabel: string = 'Undefined';

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      groupId: number,
      subgroup: GroupCompSubgroupViewModel | null,
      position: GroupCompCrewPositionViewModel | null
    },
    private dialogRef: MatDialogRef<CreateOrEditPositionPopupComponent>,
    private compositionApi: GroupCompositionApiService,
    private managementInteract: GroupManagementInteractService
  ) {}

  ngOnInit() {
    this.compositionApi.getAvailableRoleClassifications(this.data.groupId).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (classifications: RoleClassSummaryViewModel[]) => {
          this.availableClasses = classifications;
          this.initializeContext();
        },
        error: () => {
          this.managementInteract.showSnackBarMessage('Failed to fetch reference data.')
        }
      })

    this.roleCategoryCtrl.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(category => {
        this.filteredClasses = this.availableClasses.filter(c => c.roleCategory === category);

        if (category == null) {
          this.selectedRoleCtrl.reset();
          this.selectedRoleCtrl.disable();
        } else {
          this.selectedRoleCtrl.enable();
        }
      });
  }

  initializeContext() {
    if(this.data.subgroup !== null) {
      this.subgroupLabel = this.data.subgroup.subgroupLabel;
      this.contextTitle = 'Add New Crew Position';
    } else if(this.data.position !== null) {
      this.subgroupLabel = this.data.position.subgroupLabel;
      this.contextTitle = 'Edit Crew Position';

      const category = this.data.position.groupRole.roleCategory
      this.filteredClasses = this.availableClasses.filter(c => c.roleCategory === category);

      this.roleCategoryCtrl.setValue(this.data.position.groupRole.roleCategory);
      this.roleCategoryCtrl.markAsTouched();
      this.roleCategoryCtrl.markAsDirty();

      this.selectedRoleCtrl.enable();
      this.selectedRoleCtrl.setValue(this.data.position.groupRole);
      this.selectedRoleCtrl.markAsTouched();
      this.selectedRoleCtrl.markAsDirty();

      this.positionNoteCtrl.setValue(this.data.position.positionNote);
    } else {
      this.managementInteract.showSnackBarMessage('Error: Could not identify action subject.')
      this.dialogRef.close(null);
    }
  }

  onConfirm(): void {
    const role = this.selectedRoleCtrl.value;
    const positionNote = this.positionNoteCtrl.value;

    if(!role) {
      this.roleCategoryCtrl.markAsTouched();
      this.roleCategoryCtrl.markAsDirty();
      return;
    }

    const subId = this.data.subgroup !== null ? this.data.subgroup.subgroupId : this.data.position?.subgroupId;

    if(subId) {
      const newPosition = new NewOrEditPositionRequest(this.data.groupId, subId, role, positionNote);
      this.dialogRef.close(newPosition);
    } else {
      this.managementInteract.showSnackBarMessage('Error: Could not identify parent subgroup.');
      this.dialogRef.close(null);
    }
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
