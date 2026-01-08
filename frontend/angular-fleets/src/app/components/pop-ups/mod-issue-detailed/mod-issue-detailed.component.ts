import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ModIssueViewModel} from "../../../models/moderation/ModIssueViewModel";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";

@Component({
  selector: 'app-mod-issue-detailed',
  standalone: false,
  templateUrl: './mod-issue-detailed.component.html',
  styleUrl: './mod-issue-detailed.component.css'
})
export class ModIssueDetailedComponent {

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      issue: ModIssueViewModel,
      listing: GroupListingViewModel
    },
    private dialogRef: MatDialogRef<ModIssueDetailedComponent>,
    ){}

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
