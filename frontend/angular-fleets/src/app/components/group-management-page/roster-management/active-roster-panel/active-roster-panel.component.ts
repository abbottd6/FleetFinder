import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Observable, Subject, takeUntil} from "rxjs";
import {
  GroupManagementInteractService
} from "../../../../services/facade-services/group-management/group-management-interact.service";
import {ActiveRosterOptionsPanelComponent} from "./active-roster-options-panel/active-roster-options-panel.component";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {MemberChipComponent} from "../member-chip/member-chip.component";
import {RosterTabOptions} from "../roster-management.component";

@Component({
  selector: 'app-active-roster-panel',
  standalone: true,
  templateUrl: './active-roster-panel.component.html',
  imports: [
    ActiveRosterOptionsPanelComponent,
    NgForOf,
    AsyncPipe,
    MemberChipComponent,
  ],
  styleUrl: './active-roster-panel.component.css'
})
export class ActiveRosterPanelComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() groupId!: number;

  constructor(protected managementInteract: GroupManagementInteractService){}

  ngOnInit() {

  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly RosterTabOptions = RosterTabOptions;
}
