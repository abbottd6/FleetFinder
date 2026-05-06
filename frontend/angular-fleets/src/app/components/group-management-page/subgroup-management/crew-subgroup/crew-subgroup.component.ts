import {Component, Input} from '@angular/core';
import {
  GroupCompSubgroupViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";
import {CrewPositionChipComponent} from "../crew-position-chip/crew-position-chip.component";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-crew-subgroup',
  imports: [
    CrewPositionChipComponent,
    NgForOf
  ],
  templateUrl: './crew-subgroup.component.html',
  styleUrl: './crew-subgroup.component.css'
})
export class CrewSubgroupComponent {
  @Input() subgroup!: GroupCompSubgroupViewModel;


}
