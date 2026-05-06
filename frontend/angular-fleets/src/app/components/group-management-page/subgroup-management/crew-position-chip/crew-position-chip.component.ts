import {Component, Input} from '@angular/core';
import {
  GroupCompCrewPositionViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-crew-position-view-model";

@Component({
  selector: 'app-crew-position-chip',
  imports: [],
  templateUrl: './crew-position-chip.component.html',
  styleUrl: './crew-position-chip.component.css'
})
export class CrewPositionChipComponent {
  @Input() position!: GroupCompCrewPositionViewModel;
}
