import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subject, takeUntil} from "rxjs";
import {
  MemberManagementApiService
} from "../../../../services/api-services/group-management/member-management-api.service";
import {
  GroupManagementInteractService
} from "../../../../services/facade-services/group-management/group-management-interact.service";
import {ActiveRosterOptionsPanelComponent} from "./active-roster-options-panel/active-roster-options-panel.component";
import {AsyncPipe, NgForOf} from "@angular/common";
import {MemberChipComponent} from "../member-chip/member-chip.component";

@Component({
  selector: 'app-active-roster-panel',
  standalone: true,
  templateUrl: './active-roster-panel.component.html',
  imports: [
    ActiveRosterOptionsPanelComponent,
    NgForOf,
    AsyncPipe,
    MemberChipComponent
  ],
  styleUrl: './active-roster-panel.component.css'
})
export class ActiveRosterPanelComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  protected groupId!: number;

  constructor(protected memberManagementApi: MemberManagementApiService,
              protected managementInteract: GroupManagementInteractService){}

  ngOnInit() {
    if(this.managementInteract.sessionManager) {
      this.groupId = this.managementInteract.sessionManager?.listing.groupId;
    } else {
      return;
    }

    this.memberManagementApi.getActiveRosterGroupMembers(this.groupId).pipe(takeUntil(this.destroy$))
      .subscribe(page => {
        this.managementInteract.setActiveRoster(page);
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
