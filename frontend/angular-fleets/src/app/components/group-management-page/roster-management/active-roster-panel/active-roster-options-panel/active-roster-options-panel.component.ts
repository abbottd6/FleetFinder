import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AsyncPipe} from "@angular/common";
import {
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {ActiveMemberFilterState} from "../active-roster-panel.component";
import { BehaviorSubject } from "rxjs";

@Component({
  selector: 'app-active-roster-options-panel',
    imports: [
        AsyncPipe,
        MatAccordion,
        MatButtonToggle,
        MatButtonToggleGroup,
        MatExpansionPanel,
        MatExpansionPanelHeader,
        MatExpansionPanelTitle
    ],
  templateUrl: './active-roster-options-panel.component.html',
  styleUrl: './active-roster-options-panel.component.css'
})
export class ActiveRosterOptionsPanelComponent {
  @Input() activeMemberFilterState$!: BehaviorSubject<ActiveMemberFilterState>;

  @Output() emitFilterState = new EventEmitter<ActiveMemberFilterState>;

  constructor(){}

  onRoleFilterChange(roleStatus: ActiveMemberFilterState['roleStatus']) {
    this.activeMemberFilterState$.next({
      ...this.activeMemberFilterState$.getValue(),
      roleStatus
    })

    this.emitFilterState.emit(this.activeMemberFilterState$.getValue());
  }

  onCommsFilterChange(comms: ActiveMemberFilterState['comms']) {
    this.activeMemberFilterState$.next({
      ...this.activeMemberFilterState$.getValue(),
      comms
    })

    this.emitFilterState.emit(this.activeMemberFilterState$.getValue());
  }

  onRsvpFilterChange(rsvpStatus: ActiveMemberFilterState['rsvpStatus']) {
    this.activeMemberFilterState$.next({
      ...this.activeMemberFilterState$.getValue(),
      rsvpStatus
    })

    this.emitFilterState.emit(this.activeMemberFilterState$.getValue());
  }
}
