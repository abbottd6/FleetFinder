import {Component, Inject} from '@angular/core';
import {FormControl} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-user-delete-account-popup',
  standalone: false,
  templateUrl: './user-delete-account-popup.component.html',
  styleUrl: './user-delete-account-popup.component.css'
})
export class UserDeleteAccountPopupComponent {

  confirmDeleteAccount: FormControl<boolean | null> = new FormControl<boolean | null>(false);

  constructor(
    private dialogRef: MatDialogRef<UserDeleteAccountPopupComponent>,
  ) {}

  onConfirm(): void {
    this.dialogRef.close(this.confirmDeleteAccount.value ?? false);
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }

}
