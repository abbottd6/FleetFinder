import { Component } from '@angular/core';
import {AsyncPipe} from "@angular/common";
import {
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";

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

}
