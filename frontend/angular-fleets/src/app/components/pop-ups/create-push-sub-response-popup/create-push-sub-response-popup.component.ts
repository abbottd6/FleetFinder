import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {PushSubViewModel} from "../../../models/NotificationPrefAndCustomNotesModels/PushSubViewModel";
import {HttpErrorResponse} from "@angular/common/http";
import {response} from "express";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-create-push-sub-success-popup',
  standalone: true,
  templateUrl: './create-push-sub-response-popup.component.html',
  imports: [
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle,
    NgIf
  ],
  styleUrl: './create-push-sub-response-popup.component.css'
})
export class CreatePushSubResponsePopupComponent {
  protected success: PushSubViewModel | null = null;
  protected failure: HttpErrorResponse | null = null;

  constructor(
    @Inject(MAT_DIALOG_DATA)
      data: {
        response?: PushSubViewModel,
        error?: HttpErrorResponse},
    private dialogRef: MatDialogRef<CreatePushSubResponsePopupComponent>,
  ) {
    this.success = data.response ?? null;
    this.failure = data.error ?? null;
  }

  onConfirm(): void {
    this.dialogRef.close();
  }

  protected readonly HttpErrorResponse = HttpErrorResponse;
}
