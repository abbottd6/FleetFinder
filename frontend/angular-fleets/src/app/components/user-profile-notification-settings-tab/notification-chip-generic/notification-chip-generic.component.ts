import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DatePipe, NgIf} from "@angular/common";
import {NotificationViewModel} from "../../../models/NotificationViewModel";
import {MatIcon} from "@angular/material/icon";
import {Router} from "@angular/router";
import {toTitleCase} from "../../../utils/global-functions";

export const NotificationType = {
  VIS_STATUS: 'LISTING_VIS_STATUS_CHANGED',
  NEW_INVITE: 'NEW_GROUP_INVITE',
  NEW_MEMBER: 'NEW_GROUP_MEMBER',
  MEMBER_LEFT: 'GROUP_MEMBER_LEFT',
  REMOVED_FROM_GROUP: 'REMOVED_FROM_GROUP',
  NEW_LISTING_MATCH: 'NEW_LISTING_MATCH',
  LISTING_ARCHIVED: 'LISTING_ARCHIVED',
  MOD_DELETE: 'MOD_DELETE',
  OTHER: 'OTHER'
} as const;

export type NotificationType = (typeof NotificationType)[keyof typeof NotificationType];


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

  protected noteType!: String | undefined;
  protected header!: String | undefined;
  protected contextLabel!: String | undefined;
  protected context!: String | undefined;
  protected contextElLabel!: String | undefined;
  protected contextElStatus!: String | undefined;
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

      case (NotificationType.VIS_STATUS):
        this.header = "Listing visibility status changed";

        if(this.inputNote.message.length > 42) {
          this.targetTitle = "Your listing: " + "\"" + this.inputNote.message.substring(0, 42) + "\"";
        } else {
          this.targetTitle = "Your listing: " + "\"" + this.inputNote.message + "\""
        }

        this.contextLabel = "New status: ";
        this.context = toTitleCase(this.inputNote.targetMetadata?.addContext ?? '');
        break;


      case (NotificationType.NEW_INVITE):
        this.header = this.inputNote.title;
        if(this.inputNote.message.length > 50) {
          this.targetTitle = "For Group: " + "\"" + this.inputNote.message.substring(0, 47) + "...\"";
        } else {
          this.targetTitle = "For Group: " + "\"" + this.inputNote.message + "\"";
        }

        if(this.inputNote.entityNewStatus === 'REQUEST') {
          this.noteType = 'Group Join Request:';
          this.hasLink = 'user-account/groups?section=memberships';
        } else {
          this.noteType = 'Group Invite:';
          this.hasLink = 'user-account/groups?section=invites';
        }

        this.contextElLabel = "Group Status: ";
        this.contextElStatus = this.inputNote.targetMetadata?.contextElementStatus;

        this.contextLabel = "For roster: ";
        this.context = toTitleCase(this.inputNote.targetMetadata?.targetLabel ?? '');
        break;


      case (NotificationType.NEW_MEMBER):
        this.header = this.inputNote.title;

        if(this.inputNote.targetMetadata?.addContext === 'REQUEST') {
          this.noteType = 'Added to Group:'
          this.contextLabel = "Roster: ";
          this.context = toTitleCase(this.inputNote.entityNewStatus);
        } else {
          this.noteType = 'New Group Member:'
          this.contextLabel = "Member: ";
          this.context = this.inputNote.targetMetadata?.targetLabel;
        }

        this.targetTitle = this.inputNote.message.length > 45 ? this.inputNote.message.substring(0, 43) + '...'
          : this.inputNote.message;
        this.targetTitle = "In Group: " + "\"" + this.targetTitle + "\"";

        this.contextElLabel = "Group Status: ";
        this.contextElStatus = this.inputNote.targetMetadata?.contextElementStatus;

        this.hasLink = 'user-account/groups?section=memberships';
        break;


      case (NotificationType.MEMBER_LEFT):
        this.noteType = 'Member Left Your Group: '

        this.header = this.inputNote.title;
        this.targetTitle = "From Group: " + "\"" + this.inputNote.message + "\"";

        this.contextLabel = "User: ";
        this.context = this.inputNote.targetMetadata?.targetLabel;

        this.contextElLabel = "From Roster: ";
        this.contextElStatus = toTitleCase(this.inputNote.targetMetadata?.targetStatus ?? '');

        this.hasLink = "user-account/groups?section=memberships"

        break;

      case (NotificationType.REMOVED_FROM_GROUP):
        this.noteType = 'Removed from Group: ';

        this.header = this.inputNote.title;

        if(this.inputNote.targetMetadata?.targetLabel && this.inputNote.targetMetadata?.targetLabel.length > 42) {
          this.targetTitle = "From Group: " + "\"" + this.inputNote.targetMetadata?.targetLabel.substring(0, 42) + "\"";
        } else {
          this.targetTitle = "From Group: " + "\"" + this.inputNote.targetMetadata?.targetLabel;
        }

        break;

      case (NotificationType.NEW_LISTING_MATCH):
        this.header = this.inputNote.title;

        if(this.inputNote.message.length > 42) {
          this.targetTitle = "Your listing: " + "\"" + this.inputNote.message.substring(0, 42) + "\"";
        } else {
          this.targetTitle = "Listing title: " + "\"" + this.inputNote.message + "\""
        }

        this.noteType = 'Listing Match: '
        this.contextLabel = "Group Status: ";
        this.context = this.inputNote.targetMetadata?.addContext;
        this.hasLink = "listing-details/" + this.inputNote.targetMetadata?.targetId
        break;


      case (NotificationType.LISTING_ARCHIVED):
        this.header = this.inputNote.title;

        if(this.inputNote.message.length > 42) {
          this.targetTitle = "Your listing: " + "\"" + this.inputNote.message.substring(0, 42) + "\"";
        } else {
          this.targetTitle = "Your listing: " + "\"" + this.inputNote.message + "\""
        }
        break;


      case (NotificationType.MOD_DELETE):
        this.header = "Listing removed by a moderator";

        if(this.inputNote.message.length > 42) {
          this.targetTitle = "Your listing: " + "\"" + this.inputNote.message.substring(0, 42) + "\"";
        } else {
          this.targetTitle = "Your listing: " + "\"" + this.inputNote.message + "\""
        }

        this.noteType = 'Archived: '
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
    this.router.navigateByUrl(url, { onSameUrlNavigation: 'reload' });
  }

  deleteThisNotification() {
    this.deleteNoteEvent.emit(this.inputNote.notificationId);
  }

  protected readonly NotificationType = NotificationType;
}
