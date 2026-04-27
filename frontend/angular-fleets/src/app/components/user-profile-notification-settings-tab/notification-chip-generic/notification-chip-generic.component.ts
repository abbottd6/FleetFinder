import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DatePipe, NgIf} from "@angular/common";
import {NotificationViewModel} from "../../../models/NotificationViewModel";
import {MatIcon} from "@angular/material/icon";
import {Router} from "@angular/router";
import {toTitleCase} from "../../../utils/global-functions";

@Component({
  selector: 'app-notification-chip-generic',
  standalone: true,
  templateUrl: './notification-chip-generic.component.html',
  imports: [
    DatePipe,
    MatIcon,
    NgIf,
  ],
  styleUrl: './notification-chip-generic.component.css'
})
export class NotificationChipGenericComponent implements OnInit {
  @Input() inputNote!: NotificationViewModel;
  @Output() deleteNoteEvent = new EventEmitter<number>();

  protected header!: String | undefined;
  protected contextLabel!: String | undefined;
  protected context!: String | undefined;
  protected targetTitle!: String | undefined;
  protected archiveDate!: Date | undefined;
  protected isExpired: boolean = false;
  protected hasLink: string | null = null;

  constructor(private router: Router){};

  ngOnInit() {
    this.buildNoteDisplay();
  }

  buildNoteDisplay() {
    switch (this.inputNote.type) {
      case ('LISTING_VIS_STATUS_CHANGED'):
        this.header = "Listing visibility status changed";

        if(this.inputNote.message.length > 42) {
          this.targetTitle = "Your listing: " + "\"" + this.inputNote.message.substring(0, 42) + "\"";
        } else {
          this.targetTitle = "Your listing: " + "\"" + this.inputNote.message + "\""
        }

        this.contextLabel = "New status: ";
        this.context = toTitleCase(this.inputNote.targetMetadata?.addContext ?? '');
        break;

      case ('NEW_GROUP_INVITE'):
        this.header = this.inputNote.title;
        if(this.inputNote.message.length > 50) {
          this.targetTitle = "For Group: " + "\"" + this.inputNote.targetMetadata?.targetLabel.substring(0, 47) + "...\"";
        } else {
          this.targetTitle = "For Group: " + "\"" + this.inputNote.targetMetadata?.targetLabel;
        }

        this.contextLabel = "For roster: ";
        this.context = toTitleCase(this.inputNote.targetMetadata?.addContext ?? '');
        break;

      case ('NEW_GROUP_MEMBER'):
        break;

      case ('NEW_LISTING_MATCH'):
        this.header = this.inputNote.title;

        if(this.inputNote.message.length > 42) {
          this.targetTitle = "Your listing: " + "\"" + this.inputNote.message.substring(0, 42) + "\"";
        } else {
          this.targetTitle = "Listing title: " + "\"" + this.inputNote.message + "\""
        }

        this.contextLabel = "Group Status: ";
        this.context = this.inputNote.targetMetadata?.addContext;
        this.hasLink = "listing-details/" + this.inputNote.targetMetadata?.targetId
        break;

      case ('LISTING_ARCHIVED'):
        this.header = this.inputNote.title;

        if(this.inputNote.message.length > 42) {
          this.targetTitle = "Your listing: " + "\"" + this.inputNote.message.substring(0, 42) + "\"";
        } else {
          this.targetTitle = "Your listing: " + "\"" + this.inputNote.message + "\""
        }
        break;


      case ('MOD_DELETE'):
        this.header = "Listing removed by a moderator";

        if(this.inputNote.message.length > 42) {
          this.targetTitle = "Your listing: " + "\"" + this.inputNote.message.substring(0, 42) + "\"";
        } else {
          this.targetTitle = "Your listing: " + "\"" + this.inputNote.message + "\""
        }

        this.contextLabel = "Performed by: ";
        this.context = this.inputNote.targetMetadata?.targetLabel;
        break;
    }

    if (this.inputNote.message.includes("EXPIRED")) {
      this.isExpired = true;
      this.archiveDate = new Date(this.inputNote.createdAt);
      this.archiveDate.setDate(this.archiveDate.getDate() + 11);

      this.header = "Listing visibility expired."
      this.contextLabel = "Archival on: "
    }
  }

  routeLink(url: string) {
    this.router.navigateByUrl(url);
  }

  deleteThisNotification() {
    this.deleteNoteEvent.emit(this.inputNote.notificationId);
  }
}
