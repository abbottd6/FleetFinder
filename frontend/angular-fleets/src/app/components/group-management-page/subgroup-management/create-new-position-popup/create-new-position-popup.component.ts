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
import {
  GenericSmallInputFieldComponent
} from "../../../input-fields/generic-small-input-field/generic-small-input-field.component";
import {MatCheckbox} from "@angular/material/checkbox";
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
  NewCrewPositionRequest
} from "../../../../models/group-management-models/request-models/new-crew-position-request";

@Component({
  selector: 'app-create-new-position-popup',
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
  templateUrl: './create-new-position-popup.component.html',
  styleUrl: './create-new-position-popup.component.css'
})
export class CreateNewPositionPopupComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  readonly roleCategories: string[] = [
    'Ship Crew',
    'Ground Crew',
    'Support',
    'Command',
  ];

  roleCategoryCtrl: FormControl<string | null> = new FormControl<string | null>(null, {nonNullable: true});
  selectedRoleCtrl: FormControl<RoleClassSummaryViewModel | null> = new FormControl<RoleClassSummaryViewModel | null>({
    value: null, disabled: true}, {nonNullable: true});
  positionNoteCtrl: FormControl<string | null> = new FormControl<string | null>(null);

  protected availableClasses: RoleClassSummaryViewModel[] = [];
  protected filteredClasses: RoleClassSummaryViewModel[] = [];

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      subgroup: GroupCompSubgroupViewModel
    },
    private dialogRef: MatDialogRef<CreateNewPositionPopupComponent>,
    private compositionApi: GroupCompositionApiService
  ) {}

  ngOnInit() {
    this.compositionApi.getAvailableRoleClassifications(this.data.subgroup.listingId).pipe(takeUntil(this.destroy$))
      .subscribe((classifications: RoleClassSummaryViewModel[]) => {
        this.availableClasses = classifications;
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

  onConfirm(): void {
    const role = this.selectedRoleCtrl.value;
    const positionNote = this.positionNoteCtrl.value;

    if(!role) {
      this.roleCategoryCtrl.markAsTouched();
      this.roleCategoryCtrl.markAsDirty();
      return;
    }

    const newPosition = new NewCrewPositionRequest(this.data.subgroup, role, positionNote);

    this.dialogRef.close(newPosition);
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
