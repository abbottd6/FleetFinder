import {Component, inject, Input, OnDestroy, OnInit} from '@angular/core';
import {BehaviorSubject, Observable, Subject, takeUntil} from "rxjs";
import {
  GroupManagementInteractService
} from "../../../../services/facade-services/group-management/group-management-interact.service";
import {ActiveRosterOptionsPanelComponent} from "./active-roster-options-panel/active-roster-options-panel.component";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {MemberChipComponent} from "../member-chip/member-chip.component";
import {RosterTabOptions} from "../roster-management.component";
import {
  GroupManagementMemberViewModel
} from "../../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {CdkDrag, CdkDropList} from "@angular/cdk/drag-drop";
import {
  DropListRegistryService
} from "../../../../services/facade-services/group-management/drop-list-registry.service";
import {
  SubgroupManagementInteractService
} from "../../../../services/facade-services/group-management/subgroup-management-interact.service";

export interface ActiveRosterFilterState {
  assignment: 'ASSIGNED' | 'UNASSIGNED' | 'BOTH',
  comms: 'HAS_COMMS' | 'NO_COMMS' | 'BOTH',
  terms: string | null;
}

@Component({
  selector: 'app-active-roster-panel',
  standalone: true,
  templateUrl: './active-roster-panel.component.html',
  imports: [
    ActiveRosterOptionsPanelComponent,
    NgForOf,
    AsyncPipe,
    MemberChipComponent,
    CdkDropList,
    NgIf,
    CdkDrag,
  ],
  styleUrl: './active-roster-panel.component.css'
})
export class ActiveRosterPanelComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() groupId!: number;

  rosterFilterState$ = new BehaviorSubject<ActiveRosterFilterState>({
    assignment: 'BOTH', comms: 'BOTH', terms: null
  })

  rosterForDisplay$!: Observable<GroupManagementMemberViewModel[]>;

  constructor(protected managementInteract: GroupManagementInteractService,
              protected dropListRegistry: DropListRegistryService,
              protected subgroupInteract: SubgroupManagementInteractService){}

  ngOnInit() {

  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly RosterTabOptions = RosterTabOptions;
}
