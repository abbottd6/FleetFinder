import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {reportOption} from "../../../services/api-services/listing-reports-api/listing-report-api.service";
import {FormControl} from "@angular/forms";

@Component({
  selector: 'app-confirm-report',
  standalone: false,
  templateUrl: './confirm-report.component.html',
  styleUrls: [
    './confirm-report.component.css',
    '../../input-fields/search-bar/search-bar.component.css'
  ]
})
export class ConfirmReportComponent {

  reportBasisCtrl: FormControl<reportOption | null> = new FormControl<reportOption | null>(null);

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      listing: GroupListingViewModel,
      options: reportOption[]
    },
    private dialogRef: MatDialogRef<ConfirmReportComponent>,
  ) {}

  onConfirm(): void {
    const selected = this.reportBasisCtrl.value;
    this.dialogRef.close(selected);
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }
}
