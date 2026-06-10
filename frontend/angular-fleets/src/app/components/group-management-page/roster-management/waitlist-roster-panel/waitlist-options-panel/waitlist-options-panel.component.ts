import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AsyncPipe} from "@angular/common";
import {
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {WaitlistMemberFilterState} from "../waitlist-roster-panel.component";
import { BehaviorSubject } from "rxjs";

@Component({
  selector: 'app-waitlist-options-panel',
    imports: [
        AsyncPipe,
        MatAccordion,
        MatButtonToggle,
        MatButtonToggleGroup,
        MatExpansionPanel,
        MatExpansionPanelHeader,
        MatExpansionPanelTitle
    ],
  templateUrl: './waitlist-options-panel.component.html',
  styleUrl: './waitlist-options-panel.component.css'
})
export class WaitlistOptionsPanelComponent {
    @Input() waitlistMemberFilterState$!: BehaviorSubject<WaitlistMemberFilterState>;

    @Output() emitFilterState = new EventEmitter<WaitlistMemberFilterState>;

    constructor() {};

    onCommsFilterChange(comms: WaitlistMemberFilterState['comms']) {
      this.waitlistMemberFilterState$.next({
        ...this.waitlistMemberFilterState$.getValue(),
        comms
      })

      this.emitFilterState.emit(this.waitlistMemberFilterState$.getValue());
    }
}
