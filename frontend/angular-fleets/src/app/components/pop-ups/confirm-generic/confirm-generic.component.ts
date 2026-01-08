import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {reportOption} from "../../../services/api-services/listing-reports-api/listing-report-api.service";

@Component({
  selector: 'app-confirm-generic',
  standalone: false,
  templateUrl: './confirm-generic.component.html',
  styleUrl: './confirm-generic.component.css'
})
export class ConfirmGenericComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      message: string,
      title: string,
    },
    private dialogRef: MatDialogRef<ConfirmGenericComponent>,
  ) {}

  onConfirm(): void {
    this.dialogRef.close(true);
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }
}
