import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DatePipe} from "@angular/common";
import {NotificationViewModel} from "../../../models/NotificationViewModel";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-notification-chip-generic',
  standalone: true,
  templateUrl: './notification-chip-generic.component.html',
  imports: [
    DatePipe,
    MatIcon
  ],
  styleUrl: './notification-chip-generic.component.css'
})
export class NotificationChipGenericComponent implements OnInit {
  @Input() inputNote!: NotificationViewModel;
  @Output() deleteNoteEvent = new EventEmitter<number>();

  protected header!: String | undefined;
  protected contentTitle!: String | undefined;
  protected message!: String | undefined;
  protected archiveDate!: Date | undefined;
  protected isExpired: boolean = false;

  ngOnInit() {
    this.buildNoteDisplay();
  }

  buildNoteDisplay() {
    switch (this.inputNote.type) {
      case ('LISTING_VIS_STATUS_CHANGED'):
        this.header = "Listing visibility status changed";
        this.contentTitle ='Your Listing: ' + this.inputNote.title ;
        this.message = "New status: ";
        break;
      case ('LISTING_ARCHIVED'):
        this.header = "Listing Archived";
        break;
      case ('MOD_DELETE'):
        this.header = "Listing removed by a moderator";
        this.message = "Basis for removal: ";
        break;
    }

    if (this.inputNote.message.includes("EXPIRED")) {
      this.isExpired = true;
      this.archiveDate = new Date(this.inputNote.createdAt);
      this.archiveDate.setDate(this.archiveDate.getDate() + 11);

      this.header = "Listing visibility expired"
      this.message = "Archival on: "
    }
  }

  deleteThisNotification() {
    this.deleteNoteEvent.emit(this.inputNote.notificationId);
  }
}
