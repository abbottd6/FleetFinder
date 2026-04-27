import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatTab, MatTabChangeEvent, MatTabContent, MatTabGroup} from "@angular/material/tabs";
import {AsyncPipe, NgIf} from "@angular/common";
import {ActiveRosterPanelComponent} from "./active-roster-panel/active-roster-panel.component";
import {BehaviorSubject, Subject} from "rxjs";
import {
  MemberManagementApiService
} from "../../../services/api-services/group-management/member-management-api.service";
import {
  GroupManagementInteractService
} from "../../../services/facade-services/group-management/group-management-interact.service";
import {RosterInvitePanelComponent} from "./roster-invite-panel/roster-invite-panel.component";
import {WaitlistRosterPanelComponent} from "./waitlist-roster-panel/waitlist-roster-panel.component";
import {RouterLink} from "@angular/router";
import {ChatHostService} from "../../../services/facade-services/chat/chat-host.service";

export enum RosterTabOptions {
  Active = 'Active',
  Invite = 'Invite',
  Waitlist = 'Waitlist'
}

@Component({
    selector: 'app-roster-management',
    standalone: true,
    templateUrl: './roster-management.component.html',
  imports: [
    MatIcon,
    MatTab,
    MatTabContent,
    MatTabGroup,
    NgIf,
    ActiveRosterPanelComponent,
    RosterInvitePanelComponent,
    WaitlistRosterPanelComponent,
    RouterLink,
    AsyncPipe,
  ],
    styleUrl: './roster-management.component.css'
})
export class RosterManagementComponent implements OnInit, OnDestroy{
  private destroy$ = new Subject<void>();

  groupId!: number;

  public selectedTab$ = new BehaviorSubject<RosterTabOptions | null>(RosterTabOptions.Active);

  constructor(private memberManagementApi: MemberManagementApiService,
              protected managementInteract: GroupManagementInteractService,
              protected chatHostSrv: ChatHostService) {}

  ngOnInit() {
    if(this.managementInteract.sessionManager) {
      this.groupId = this.managementInteract.sessionManager?.listing.groupId;
    } else {
      return;
    }

    this.selectedTab$.next(RosterTabOptions.Active);
    this.handleTabLoad(0);
  }

  onTabSwitch(event: MatTabChangeEvent) {
    this.selectedTab$.next(null);
    this.handleTabLoad(event.index)
  }

  handleTabLoad(idx: number) {
    if (idx === 0) {
      this.selectedTab$.next(RosterTabOptions.Active);
      this.managementInteract.fetchActiveRoster(this.groupId);
    } else if (idx === 1) {
      this.selectedTab$.next(RosterTabOptions.Invite);
      this.managementInteract.fetchGroupInvites(this.groupId);
    } else if (idx === 2) {
      this.selectedTab$.next(RosterTabOptions.Waitlist);
      this.managementInteract.fetchWaitlistRoster(this.groupId);
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly RosterTabOptions = RosterTabOptions;
}
