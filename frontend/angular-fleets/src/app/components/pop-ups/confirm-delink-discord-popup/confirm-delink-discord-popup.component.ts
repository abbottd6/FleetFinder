import { Component } from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-confirm-delink-discord-popup',
  standalone: false,
  templateUrl: './confirm-delink-discord-popup.component.html',
  styleUrl: './confirm-delink-discord-popup.component.css'
})
export class ConfirmDelinkDiscordPopupComponent {

  constructor(
    private dialogRef: MatDialogRef<ConfirmDelinkDiscordPopupComponent>,
  ) {}

  onConfirm(): void {
    this.dialogRef.close(true);
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
