import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subject, takeUntil} from "rxjs";
import {
  MemberManagementApiService
} from "../../../../services/api-services/group-management/member-management-api.service";
import {
  GroupManagementInteractService
} from "../../../../services/facade-services/group-management/group-management-interact.service";
import {InviteOptionsPanelComponent} from "./invite-options-panel/invite-options-panel.component";
import {AsyncPipe, NgForOf} from "@angular/common";
import {InviteChipComponent} from "./invite-chip/invite-chip.component";

@Component({
  selector: 'app-roster-invite-panel',
  standalone: true,
  templateUrl: './roster-invite-panel.component.html',
  imports: [
    InviteOptionsPanelComponent,
    AsyncPipe,
    InviteChipComponent,
    NgForOf
  ],
  styleUrl: './roster-invite-panel.component.css'
})
export class RosterInvitePanelComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  protected groupId!: number;

  protected noGroupInvites: boolean = true;

  constructor(private memberManagementApi: MemberManagementApiService,
              protected managementInteract: GroupManagementInteractService){}

  ngOnInit() {
    if(this.managementInteract.sessionManager) {
      this.groupId = this.managementInteract.sessionManager?.listing.groupId;
    }

    this.memberManagementApi.getGroupInvites(this.groupId).pipe(takeUntil(this.destroy$))
      .subscribe(page => {
        this.managementInteract.setGroupInvites(page);
        this.noGroupInvites = page.content.length === 0;
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
