import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatLabel} from "@angular/material/input";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {MatRadioButton, MatRadioGroup} from "@angular/material/radio";
import {InvitePanelFilterState} from "../roster-invite-panel.component";
import {BehaviorSubject} from "rxjs";
import {AsyncPipe} from "@angular/common";
import {
  GroupManagementUiPrefsService, InviteDirection, InvitePanelOptions
} from "../../../../../services/facade-services/group-management/group-management-ui-prefs/group-management-ui-prefs.service";

@Component({
  selector: 'app-invite-options-panel',
  imports: [
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelTitle,
    MatExpansionPanelHeader,
    MatLabel,
    MatSlideToggle,
    ReactiveFormsModule,
    MatRadioGroup,
    AsyncPipe,
    MatRadioButton
  ],
  templateUrl: './invite-options-panel.component.html',
  styleUrl: './invite-options-panel.component.css'
})
export class InviteOptionsPanelComponent implements OnInit {

  @Input() inviteFilterState$!: BehaviorSubject<InvitePanelFilterState>;

  @Output() emitFilterState = new EventEmitter<InvitePanelFilterState>();
  protected showPendingOnlyCtrl = new FormControl<boolean>(true);

  constructor(private mgmtUiPrefs: GroupManagementUiPrefsService){}

  ngOnInit() {
    if(this.mgmtUiPrefs.storedInviteFilters.status == 'BOTH') {
      this.showPendingOnlyCtrl.setValue(false)
    }
  }

  onStatusChange() {
    this.inviteFilterState$.next({
      ...this.inviteFilterState$.getValue(),
      status: this.showPendingOnlyCtrl.value == true ? 'PENDING' : 'BOTH'
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
