import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormControl} from "@angular/forms";

@Component({
  selector: 'app-confirm-clear-issue',
  standalone: false,
  templateUrl: './confirm-clear-issue.component.html',
  styleUrl: './confirm-clear-issue.component.css'
})
export class ConfirmClearIssueComponent {
  modNote: FormControl<string | null> = new FormControl<string | null>("Manual clear");
  characterCount: number = 0;

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      title: string
    },
    private dialogRef: MatDialogRef<ConfirmClearIssueComponent>,
  ) {}

  updateCharacterCount() {
    const value = this.modNote.value || '';
    this.characterCount = value.length;
  }

  onConfirm(): void {
    const selected = this.modNote.value;
    this.dialogRef.close(this.modNote.value);
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }
}
