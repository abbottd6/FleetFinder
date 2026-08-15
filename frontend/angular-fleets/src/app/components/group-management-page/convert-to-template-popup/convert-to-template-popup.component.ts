import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subject} from "rxjs";
import {FormControl, Validators} from "@angular/forms";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {
  GroupCompositionDto
} from "../../../models/group-management-models/view-models/group-composition/group-composition-dto";
import {
  GenericSmallInputFieldComponent
} from "../../input-fields/generic-small-input-field/generic-small-input-field.component";
import {
  GroupCompSubgroupViewModel
} from "../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";
import {
  TemplateFromCompRequest
} from "../../../models/group-management-models/request-models/template-from-comp-request";

@Component({
  selector: 'app-convert-to-template-popup',
  imports: [
    GenericSmallInputFieldComponent,
    MatDialogContent,
    MatDialogTitle,
    MatDialogActions
  ],
  templateUrl: './convert-to-template-popup.component.html',
  styleUrl: './convert-to-template-popup.component.css'
})
export class ConvertToTemplatePopupComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  protected isFullCompClone!: boolean;

  templateLabelCtrl: FormControl<string | null> = new FormControl<string | null>('', {
    nonNullable: true,
    validators: [Validators.required,
      Validators.minLength(1),
      Validators.maxLength(32)]
  });

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      listingId: number,
      templateRootReferenceLabel: string | null,
      subgroups: GroupCompSubgroupViewModel[]
    },
    private dialogRef: MatDialogRef<ConvertToTemplatePopupComponent>
  ){}

  ngOnInit() {
    console.log("in popup: " + this.data.templateRootReferenceLabel);
    this.isFullCompClone = (this.data.subgroups.length > 1);
  }

  onConfirm() {
    if(this.templateLabelCtrl.invalid || this.templateLabelCtrl.value == '' || this.templateLabelCtrl.value === null) {
      this.templateLabelCtrl.markAsTouched();
      this.templateLabelCtrl.markAsDirty();
      return;
    }

    const subgroupLabel = this.templateLabelCtrl.value;

    const request: TemplateFromCompRequest = new TemplateFromCompRequest(this.data.listingId, subgroupLabel, this.data.subgroups);

    this.dialogRef.close(request);
  }

  onCancel() {
    this.dialogRef.close(null);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
