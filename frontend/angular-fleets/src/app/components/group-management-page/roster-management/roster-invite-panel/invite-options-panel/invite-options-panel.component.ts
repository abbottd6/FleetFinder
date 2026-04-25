import {Component, EventEmitter, Input, Output} from '@angular/core';
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";
import {ReactiveFormsModule} from "@angular/forms";
import {MatRadioButton, MatRadioGroup} from "@angular/material/radio";
import {InvitePanelFilterState} from "../roster-invite-panel.component";
import {BehaviorSubject} from "rxjs";
import {AsyncPipe} from "@angular/common";

@Component({
  selector: 'app-invite-options-panel',
  imports: [
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelTitle,
    MatExpansionPanelHeader,
    ReactiveFormsModule,
    MatRadioGroup,
    AsyncPipe,
    MatRadioButton
  ],
  templateUrl: './invite-options-panel.component.html',
  styleUrl: './invite-options-panel.component.css'
})
export class InviteOptionsPanelComponent {

  @Input() inviteFilterState$!: BehaviorSubject<InvitePanelFilterState>;

  @Output() emitFilterState = new EventEmitter<InvitePanelFilterState>();

  constructor(){}

  onStatusChange(status: InvitePanelFilterState['status']) {
    this.inviteFilterState$.next({
      ...this.inviteFilterState$.getValue(),
      status
    })
    this.emitFilterState.emit(this.inviteFilterState$.getValue());
  }

  onDirectionChange(direction: InvitePanelFilterState['direction']) {
    this.inviteFilterState$.next({
      ...this.inviteFilterState$.getValue(),
      direction
    })
    this.emitFilterState.emit(this.inviteFilterState$.getValue());
  }
}
