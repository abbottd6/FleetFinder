import { Component } from '@angular/core';
import {
  MatAccordion,
  MatExpansionPanel, MatExpansionPanelDescription,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";

@Component({
  selector: 'app-notification-options-accordion',
  standalone: true,
  templateUrl: './notification-options-accordion.component.html',
  imports: [
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatExpansionPanelDescription
  ],
  styleUrl: './notification-options-accordion.component.css'
})
export class NotificationOptionsAccordionComponent {

}
