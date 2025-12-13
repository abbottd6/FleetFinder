import {Component, Inject} from '@angular/core';
import {FormControl} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-hide-how-to-popup',
  standalone: false,
  templateUrl: './dont-show-me-again-popup.html',
  styleUrl: './dont-show-me-again-popup.css'
})
export class DontShowMeAgainPopup {

  dontShowMe: FormControl<boolean | null> = new FormControl<boolean | null>(false);

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      message: string;
    },
    private dialogRef: MatDialogRef<DontShowMeAgainPopup>,
  ) {}

  onConfirm(): void {
    this.dialogRef.close(this.dontShowMe.value ?? false);
  }
}
