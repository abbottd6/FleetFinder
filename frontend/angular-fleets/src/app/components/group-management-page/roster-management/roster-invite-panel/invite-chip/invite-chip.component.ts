import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
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
  RESCIND: 'RESCINDED',
  ACCEPT: 'ACCEPTED',
  DECLINE: 'DECLINED',
  MESSAGE: 'MESSAGE',
  WAITLIST: 'WAITLIST',
  DISMISS: 'DISMISS',
} as const;

export type InviteActions = typeof InviteActions[keyof typeof InviteActions];

export type InviteStatusChange = {
  newStatus: InviteActions,
  newInvite: GroupManagementInviteViewModel
}

@Component({
  selector: 'app-invite-chip',
  standalone: true,
  templateUrl: './invite-chip.component.html',
  imports: [
    NgIf,
    MatIcon,
    MatTooltip,
    TitleCasePipe,
  ],
  styleUrl: './invite-chip.component.css'
})
export class InviteChipComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  protected readonly inviteActions = InviteActions;

  protected inviteMember!: UserMonikerSummaryViewModel;

  protected isHovered: boolean = false;

  @Output() inviteStatusChangeEmitter = new EventEmitter<InviteStatusChange>();
  @Output() inviteAltActionEmitter = new EventEmitter<InviteActions>();

  @Input() invite!: GroupManagementInviteViewModel;

  constructor(private dialog: MatDialog){}

  ngOnInit() {
    if(this.invite.inviteDirection === 'REQUEST') {
      this.inviteMember = this.invite.senderSummary;
    } else {
      this.inviteMember = this.invite.recipientSummary;
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

  emitInviteStatusChange(action: InviteActions) {
    this.invite.inviteStatus = action;
    const statusChange: InviteStatusChange = { newStatus: action, newInvite: this.invite};
    this.inviteStatusChangeEmitter.emit(statusChange);
  }

  emitInviteAltAction(action: InviteActions) {

  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
