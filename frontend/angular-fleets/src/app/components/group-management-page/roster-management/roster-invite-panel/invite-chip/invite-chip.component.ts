import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subject} from "rxjs";
import {
  GroupManagementInviteViewModel
} from "../../../../../models/group-management-models/view-models/group-membership/group-management-invite-view-model";
import {DatePipe, NgIf, TitleCasePipe} from "@angular/common";
import {
  UserMonikerSummaryViewModel
} from "../../../../../models/group-management-models/nested-models/user-moniker-summary-view-model";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";

@Component({
  selector: 'app-invite-chip',
  standalone: true,
  templateUrl: './invite-chip.component.html',
  imports: [
    NgIf,
    MatIcon,
    MatTooltip,
    TitleCasePipe
  ],
  styleUrl: './invite-chip.component.css'
})
export class InviteChipComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  protected inviteMember!: UserMonikerSummaryViewModel;

  @Input() invite!: GroupManagementInviteViewModel;

  ngOnInit() {
    if(this.invite.inviteDirection === 'REQUEST') {
      this.inviteMember = this.invite.senderSummary;
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
