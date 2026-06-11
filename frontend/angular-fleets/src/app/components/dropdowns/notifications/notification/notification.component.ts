import {Component, Input, OnInit} from '@angular/core';
import {NotificationViewModel} from "../../../../models/NotificationViewModel";
import {DatePipe, NgIf, SlicePipe} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {NotificationService} from "../../../../services/facade-services/notifications/notification.service";
import {Router} from "@angular/router";
import {toTitleCase} from "../../../../utils/global-functions";

@Component({
  selector: 'app-notification',
  standalone: true,
  templateUrl: './notification.component.html',
  imports: [
    SlicePipe,
    MatIcon,
    DatePipe,
    NgIf
  ],
  styleUrl: './notification.component.css'
})
export class NotificationComponent implements OnInit {
  @Input() note!: NotificationViewModel;

  protected header!: String | undefined;
  protected contextLabel!: String | undefined;
  protected context!: String | undefined;
  protected archiveDate!: Date | undefined;
  protected isExpired: boolean = false;
  protected hasLink: string | null = null;

  constructor(protected noteService: NotificationService, private router: Router) {}

  ngOnInit(): void {
    this.buildNoteDisplay();
  }

  buildNoteDisplay() {
    switch (this.note.type) {
      case ('LISTING_VIS_STATUS_CHANGED'):
        this.header = "Listing visibility status changed";
        this.contextLabel = "New status: ";
        this.context = toTitleCase(this.note.targetMetadata?.addContext ?? '');
        this.hasLink = 'user-account/listings';
        break;
      case ('NEW_LISTING_MATCH'):
        this.header = this.note.title;
        this.contextLabel = "Group Status: ";
        this.context = toTitleCase(this.note.targetMetadata?.addContext ?? '');
        this.hasLink = "listing-details/" + this.note.targetMetadata?.targetId;
        break;
      case ('NEW_GROUP_INVITE'):
        if(this.note.entityNewStatus === 'REQUEST') {
          this.header = this.note.title;
          this.hasLink = 'user-account/groups?section=memberships'
        } else {
          this.header = this.note.title;
          this.hasLink = 'user-account/groups?section=invites';
        }
        this.contextLabel = "Roster: ";
        this.context = toTitleCase(this.note.targetMetadata?.targetLabel ?? '');
        break;
      case ('NEW_GROUP_MEMBER'):
        this.header = this.note.title;
        if(this.note.targetMetadata?.addContext === 'REQUEST') {
          this.contextLabel = "Roster: ";
          this.context = toTitleCase(this.note.entityNewStatus);
        } else {
          this.contextLabel = "Member: ";
          this.context = this.note.targetMetadata?.targetLabel;
        }
        this.hasLink = 'user-account/groups?section=memberships';
        break;
      case ('GROUP_MEMBER_LEFT'):
        this.header = this.note.title;
        this.contextLabel = "From Roster: ";
        this.context = toTitleCase(this.note.targetMetadata?.targetStatus ?? '');
        this.hasLink = 'user-account/groups?section=memberships';
        break;
      case ('REMOVED_FROM_GROUP'):
        this.header = this.note.title;
        break;
      case ('LISTING_ARCHIVED'):
        this.header = this.note.title;
        break;
      case ('MOD_DELETE'):
        this.header = "Listing removed by a moderator";
        this.contextLabel = "Performed by: ";
        this.context = this.note.targetMetadata?.targetLabel;
        break;
    }

    if (this.note.message.includes("EXPIRED")) {
      this.isExpired = true;
      this.archiveDate = new Date(this.note.createdAt);
      this.archiveDate.setDate(this.archiveDate.getDate() + 11);

      this.header = "Listing visibility expired."
      this.contextLabel = "Archival on: "
    }
  }

  routeLink(url: string) {
    this.router.navigateByUrl(url);
  }
}
