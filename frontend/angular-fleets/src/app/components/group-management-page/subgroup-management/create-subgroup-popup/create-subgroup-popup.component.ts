import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subject} from "rxjs";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {
  RoleClassSummaryViewModel
} from "../../../../models/group-management-models/nested-models/role-class-summary-view-model";
import {FormControl, ReactiveFormsModule, Validators} from "@angular/forms";
import {
  GenericSmallInputFieldComponent
} from "../../../input-fields/generic-small-input-field/generic-small-input-field.component";
import {
  GenericMediumInputFieldComponent
} from "../../../input-fields/generic-medium-input-field/generic-medium-input-field.component";
import {
  AddPositionToNewSubgroupFormComponent
} from "./add-position-to-new-subgroup-form/add-position-to-new-subgroup-form.component";
import {
  AddNewSubgroupRequest
} from "../../../../models/group-management-models/request-models/add-new-subgroup-request";
import {DropListOrientation} from "@angular/cdk/drag-drop";
import {
  GroupManagementInteractService
} from "../../../../services/facade-services/group-management/group-management-interact.service";

@Component({
  selector: 'app-create-subgroup-popup',
  imports: [
    MatDialogContent,
    MatDialogTitle,
    MatDialogActions,
    GenericSmallInputFieldComponent,
    GenericMediumInputFieldComponent,
    ReactiveFormsModule,
    AddPositionToNewSubgroupFormComponent
  ],
  templateUrl: './create-subgroup-popup.component.html',
  styleUrl: './create-subgroup-popup.component.css'
})
export class CreateSubgroupPopupComponent implements OnInit, OnDestroy{
  private destroy$ = new Subject<void>();

  protected popupTitle!: string;

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

  protected positions: RoleClassSummaryViewModel[] = [];

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      listingId: number,
      parentSubgroupLabel: string | null,
      rootSubgroupId: number | null,
      parentSubgroupId: number | null,
    },
    private dialogRef: MatDialogRef<CreateSubgroupPopupComponent>,
    private managementInteract: GroupManagementInteractService
  ){}

  ngOnInit() {
    if(this.data.parentSubgroupLabel) {
      this.popupTitle = 'Add New Nested Subgroup';
    } else {
      this.popupTitle = 'Add New Root Level Subgroup';
    }
  }

  addPosition(position: RoleClassSummaryViewModel) {
    if(this.positions.length < 150) {
      this.positions.push(position);
    } else {
      this.managementInteract.showSnackBarMessage('Subgroup positions limit reached.');
      return;
    }
  }

  decrementPosition(position: RoleClassSummaryViewModel) {
    const idx = this.positions.indexOf(position);
    if(idx === -1) return;

    this.positions = [
      ...this.positions.slice(0, idx),
      ...this.positions.slice(idx + 1)
    ]
  }

  get groupedPositions(): { role: RoleClassSummaryViewModel; count: number } [] {
    const map = new Map<string, {role: RoleClassSummaryViewModel; count: number}>();

    for(const p of this.positions) {
      const existing = map.get(p.roleTitle);
      if(existing) {
        existing.count++;
      } else {
        map.set(p.roleTitle, {role: p, count: 1});
      }
    }
    return [...map.values()];
  }

  onConfirm() {
    if(!this.subgroupLabelCtrl.value || this.subgroupLabelCtrl.invalid) {
      this.subgroupLabelCtrl.markAsTouched();
      this.subgroupLabelCtrl.markAsDirty();
      console.log('what is going on');
      return;
    }

    const subgroupLabel = this.subgroupLabelCtrl.value!;
    const subgroupNotes = this.subgroupNotesCtrl.value;

    const dropListOrientation: DropListOrientation = 'vertical';

    const request = new AddNewSubgroupRequest(this.data.listingId, this.data.rootSubgroupId, this.data.parentSubgroupId,
                                              subgroupLabel, subgroupNotes, dropListOrientation,
                                              this.positions);

    console.log(request);

    this.dialogRef.close(request);
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
