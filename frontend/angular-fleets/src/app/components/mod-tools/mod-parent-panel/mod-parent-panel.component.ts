import { Component } from '@angular/core';
import {MatTabsModule} from "@angular/material/tabs";

@Component({
  selector: 'app-mod-parent-panel',
  standalone: true,
  templateUrl: './mod-parent-panel.component.html',
  styleUrl: './mod-parent-panel.component.css',
  imports: [MatTabsModule],
})
export class ModParentPanelComponent {

}
