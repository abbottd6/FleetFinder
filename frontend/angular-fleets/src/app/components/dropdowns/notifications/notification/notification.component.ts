import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {NotificationViewModel} from "../../../../models/NotificationViewModel";
import {DatePipe, SlicePipe} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {NotificationService} from "../../../../services/facade-services/notifications/notification.service";

@Component({
  selector: 'app-notification',
  standalone: true,
  templateUrl: './notification.component.html',
  imports: [
    SlicePipe,
    MatIcon,
    DatePipe
  ],
  styleUrl: './notification.component.css'
})
export class NotificationComponent implements OnInit {
  @Input() note!: NotificationViewModel;

  protected header!: String | undefined;
  protected message!: String | undefined;
  protected archiveDate!: Date | undefined;
  protected isExpired: boolean = false;

  constructor(protected noteService: NotificationService) {}

  ngOnInit(): void {
    this.buildNoteDisplay();
  }

  buildNoteDisplay() {
    switch (this.note.type) {
      case ('LISTING_VIS_STATUS_CHANGED'):
        this.header = "Listing visibility status changed";
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

    if (this.note.message.includes("EXPIRED")) {
      this.isExpired = true;
      this.archiveDate = new Date(this.note.createdAt);
      this.archiveDate.setDate(this.archiveDate.getDate() + 11);

      this.header = "Listing visibility expired"
      this.message = "Archival on: "
    }
  }
}
