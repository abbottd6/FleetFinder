import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatTab, MatTabContent, MatTabGroup} from "@angular/material/tabs";
import {NgIf} from "@angular/common";
import {ActiveRosterPanelComponent} from "./active-roster-panel/active-roster-panel.component";
import {Subject} from "rxjs";
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

@Component({
    selector: 'app-roster-management',
    standalone: true,
    templateUrl: './roster-management.component.html',
  imports: [
    MatIcon,
    MatProgressSpinner,
    MatTab,
    MatTabContent,
    MatTabGroup,
    NgIf,
    ActiveRosterPanelComponent,
    RosterInvitePanelComponent,
    WaitlistRosterPanelComponent,
    RouterLink
  ],
    styleUrl: './roster-management.component.css'
})
export class RosterManagementComponent implements OnInit, OnDestroy{
  private destroy$ = new Subject<void>();

  protected rosterIsLoading: boolean = true;

  constructor(private memberManagementApi: MemberManagementApiService,
              protected managementInteract: GroupManagementInteractService,
              protected chatHostSrv: ChatHostService) {}

  ngOnInit() {
    this.rosterIsLoading = true;
    setTimeout(() => this.rosterIsLoading = false, 1000)
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
