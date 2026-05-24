import {Component, Input, OnInit} from '@angular/core';
import {
  GroupCompCrewPositionViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-crew-position-view-model";
import {CdkDragHandle, DragDropModule} from "@angular/cdk/drag-drop";
import {BehaviorSubject} from "rxjs";
import {NgForOf, NgIf} from "@angular/common";
import {MemberChipComponent} from "../../roster-management/member-chip/member-chip.component";
import {
  DropListRegistryService
} from "../../../../services/facade-services/group-management/drop-list-registry.service";
import {
  SubgroupManagementInteractService
} from "../../../../services/facade-services/group-management/subgroup-management-interact.service";
import {
  GroupManagementMemberViewModel
} from "../../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";

@Component({
  selector: 'app-crew-position-chip',
  imports: [
    DragDropModule,
    CdkDragHandle,
    MemberChipComponent,
    NgForOf
  ],
  templateUrl: './crew-position-chip.component.html',
  styleUrl: './crew-position-chip.component.css'
})
export class CrewPositionChipComponent implements OnInit {
  @Input() position!: GroupCompCrewPositionViewModel;

  protected assignedMember!: GroupManagementMemberViewModel[];

  constructor(protected dropListRegistry: DropListRegistryService,
              protected subgroupInteract: SubgroupManagementInteractService){

  }

  ngOnInit() {
    this.assignedMember = this.position?.assignedMember ? [this.position.assignedMember] : [];
  }
}
