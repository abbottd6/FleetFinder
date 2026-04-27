import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Subject} from "rxjs";
import {
  GroupManagementInviteViewModel
} from "../../../../../models/group-management-models/view-models/group-membership/group-management-invite-view-model";
import {NgClass, NgIf, TitleCasePipe} from "@angular/common";
import {
  UserMonikerSummaryViewModel
} from "../../../../../models/group-management-models/nested-models/user-moniker-summary-view-model";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {MatDialog} from "@angular/material/dialog";
import {InviteDetailsPopupComponent} from "../invite-details-popup/invite-details-popup.component"
import {
  InviteStatus
} from "../../../../../services/facade-services/group-management/group-management-ui-prefs/group-management-ui-prefs.service";

export const InviteActions = {
  RESCIND: 'RESCINDED',
  ACCEPT: 'ACCEPTED',
  DECLINE: 'DECLINED',
  MESSAGE: 'MESSAGE',
  WAITLIST: 'WAITLIST',
  DISMISS: 'DISMISS',
} as const;

export const StatusChangeActions = [
  InviteActions.ACCEPT,
  InviteActions.DECLINE,
  InviteActions.RESCIND,
  InviteActions.DISMISS
] as const

export function isStatusChangeAction(action: InviteActions) {
  return (StatusChangeActions as readonly InviteActions[]).includes(action);
}

export type InviteActions = typeof InviteActions[keyof typeof InviteActions];

export type InviteWithActionInterface = {
  action: InviteActions,
  invite: GroupManagementInviteViewModel
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
    NgClass,
  ],
  styleUrl: './invite-chip.component.css'
})
export class InviteChipComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  protected readonly inviteActions = InviteActions;

  protected inviteMember!: UserMonikerSummaryViewModel;

  protected isHovered: boolean = false;

  @Output() inviteActionEmitter = new EventEmitter<InviteWithActionInterface>();
  @Output() messageRequestEmitter = new EventEmitter<UserMonikerSummaryViewModel>;

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
    });

    dialogRef.afterClosed().subscribe((action: InviteActions | null) => {
      if(action) {
        if(isStatusChangeAction(action)) {
          this.emitInviteStatusChange(action);
        } else {
          this.emitInviteAltAction(action);
        }
      }
    })
  }

  emitInviteStatusChange(action: InviteActions) {
    this.invite.inviteStatus = action;
    const statusChange: InviteWithActionInterface = { action: action, invite: this.invite};
    this.inviteActionEmitter.emit(statusChange);
  }

  emitInviteAltAction(action: InviteActions) {
    if(action === InviteActions.MESSAGE) {
      this.messageRequestEmitter.emit(this.inviteMember);
    }
    const altAction: InviteWithActionInterface = { action: action, invite: this.invite };
    this.inviteActionEmitter.emit(altAction);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly InviteStatus = InviteStatus;
  protected readonly InviteActions = InviteActions;
}
