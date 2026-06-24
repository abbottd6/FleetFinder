import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {
  GroupCompCrewPositionViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-crew-position-view-model";
import {CdkDragHandle, DragDropModule} from "@angular/cdk/drag-drop";
import {BehaviorSubject} from "rxjs";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {MemberChipComponent} from "../../roster-management/member-chip/member-chip.component";
import {
  DropListRegistryService
} from "../../../../services/facade-services/group-management/drop-list-registry.service";
import {
  GroupCompositionInteractService
} from "../../../../services/facade-services/group-management/group-composition-interact.service";
import {
  GroupManagementMemberViewModel
} from "../../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";

@Component({
  selector: 'app-crew-position-chip',
  imports: [
    DragDropModule,
    CdkDragHandle,
    MemberChipComponent,
    NgIf,
    AsyncPipe,
    MatIcon,
    MatTooltip,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger
  ],
  templateUrl: './crew-position-chip.component.html',
  styleUrl: './crew-position-chip.component.css'
})
export class CrewPositionChipComponent implements OnInit, OnChanges {
  @Input() position!: GroupCompCrewPositionViewModel;
  @Output() emitPositionDelete = new EventEmitter<GroupCompCrewPositionViewModel>;
  @Output() emitPositionEdit = new EventEmitter<GroupCompCrewPositionViewModel>;

  protected assignedMember!: GroupManagementMemberViewModel[];

  constructor(protected dropListRegistry: DropListRegistryService,
              protected compositionInteract: GroupCompositionInteractService){

  }

  ngOnInit() {
    this.assignedMember = this.position?.assignedMember ? [this.position.assignedMember] : [];
  }

  ngOnChanges(changes: SimpleChanges) {
    if(changes['position']) {
      this.assignedMember = this.position?.assignedMember ? [this.position.assignedMember] : [];
    }
  }
}
