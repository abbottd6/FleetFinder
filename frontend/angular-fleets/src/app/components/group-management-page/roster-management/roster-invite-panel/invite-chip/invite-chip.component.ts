import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subject} from "rxjs";
import {
  GroupManagementInviteViewModel
} from "../../../../../models/group-management-models/view-models/group-membership/group-management-invite-view-model";
import {NgIf, TitleCasePipe} from "@angular/common";
import {
  UserMonikerSummaryViewModel
} from "../../../../../models/group-management-models/nested-models/user-moniker-summary-view-model";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {MatDialog} from "@angular/material/dialog";
import {InviteDetailsPopupComponent} from "../invite-details-popup/invite-details-popup.component"

export const InviteActions = {
  RESCIND: 'RESCIND',
  ACCEPT: 'ACCEPT',
  DECLINE: 'DECLINE',
  MESSAGE: 'MESSAGE',
  WAITLIST: 'WAITLIST',
} as const;

export type InviteActions = typeof InviteActions[keyof typeof InviteActions];

@Component({
  selector: 'app-invite-chip',
  standalone: true,
  templateUrl: './invite-chip.component.html',
  imports: [
    NgIf,
    MatIcon,
    MatTooltip,
  ],
  styleUrl: './invite-chip.component.css'
})
export class InviteChipComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  protected inviteMember!: UserMonikerSummaryViewModel;

  protected isHovered: boolean = false;

  @Input() invite!: GroupManagementInviteViewModel;

  constructor(private dialog: MatDialog){}

  ngOnInit() {
    if(this.invite.inviteDirection === 'REQUEST') {
      this.inviteMember = this.invite.senderSummary;
    }
  }

  openInviteDetailsPopup() {
    const dialogRef = this.dialog.open(InviteDetailsPopupComponent, {
      data: {
        invite: this.invite,
        targetMember: this.inviteMember
      }
    })
  }

  emitDeclineInviteRequest() {

  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
