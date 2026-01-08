import {Component, Inject, inject, OnChanges, SimpleChanges} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";

@Component({
  selector: 'app-confirm-delete',
  standalone: false,
  templateUrl: './confirm-delete.component.html',
  styleUrl: './confirm-delete.component.css'
})
export class ConfirmDeleteComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {rows: GroupListingViewModel[]},
    private dialogRef: MatDialogRef<ConfirmDeleteComponent>,
  ) {}

  onConfirm(): void {
    this.dialogRef.close(true);
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
