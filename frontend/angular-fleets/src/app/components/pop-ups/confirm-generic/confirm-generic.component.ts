import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

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
