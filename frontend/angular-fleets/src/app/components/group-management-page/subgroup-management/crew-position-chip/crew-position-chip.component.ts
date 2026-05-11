import {Component, Input} from '@angular/core';
import {
  GroupCompCrewPositionViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-crew-position-view-model";
import {CdkDragHandle, DragDropModule} from "@angular/cdk/drag-drop";
import {BehaviorSubject} from "rxjs";

@Component({
  selector: 'app-crew-position-chip',
  imports: [
    DragDropModule,
    CdkDragHandle
  ],
  templateUrl: './crew-position-chip.component.html',
  styleUrl: './crew-position-chip.component.css'
})
export class CrewPositionChipComponent {
  @Input() position!: GroupCompCrewPositionViewModel;


}
