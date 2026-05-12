import {DestroyRef, inject, Injectable} from '@angular/core';
import {
  GroupCompositionApiService
} from "../../api-services/group-management/group-composition-api/group-composition-api.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {
  CrewTemplateViewModel
} from "../../../models/group-management-models/view-models/group-composition/crew-template-view-model";
import {BehaviorSubject} from "rxjs";
import {
  GroupCompSubgroupViewModel
} from "../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";
import {
  GroupCompositionDto
} from "../../../models/group-management-models/view-models/group-composition/group-composition-dto";
import {CdkDrag, CdkDragDrop, moveItemInArray, transferArrayItem} from "@angular/cdk/drag-drop";
import {
  GroupCompCrewPositionViewModel
} from "../../../models/group-management-models/view-models/group-composition/group-comp-crew-position-view-model";
import {
  GroupManagementMemberViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {DropListRegistryService} from "./drop-list-registry.service";

@Injectable({
  providedIn: 'root'
})
export class SubgroupManagementInteractService {
  private destroyRef = inject(DestroyRef);
  public isDragging = false;

  protected subgroupTreesSubject = new BehaviorSubject<GroupCompSubgroupViewModel[]>([]);
  public subgroupTrees$ = this.subgroupTreesSubject.asObservable();

  constructor(private compositionApi: GroupCompositionApiService,
              private dropListRegistry: DropListRegistryService) {}

  getExistingSubgroupTrees(groupId: number) {
    this.compositionApi.getExistingGroupStructure(groupId).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (subgroupTrees: GroupCompositionDto) => {
          this.subgroupTreesSubject.next(subgroupTrees.subgroups);
        }
      })
  }

  createSubgroupFromTemplate(groupId: number, template: CrewTemplateViewModel) {
    this.compositionApi.createSubgroupFromTemplate(groupId, template).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (responseDto: GroupCompositionDto) => {
          const newSubgroups = responseDto.subgroups;
          const currentList = this.subgroupTreesSubject.getValue();
          this.subgroupTreesSubject.next([
            ...currentList,
            ...newSubgroups
          ])
        }
      });
  }

  onSubgroupDrop(event: CdkDragDrop<GroupCompSubgroupViewModel[]>) {
    if(event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);

      event.container.data.forEach((subgroup, index) => {
        subgroup.sortOrder = index;
      })
    } else {
      transferArrayItem(event.previousContainer.data, event.container.data, event.previousIndex, event.currentIndex);

      event.container.data.forEach((subgroup, index) => {
        subgroup.sortOrder = index;
      })
    }
    this.dropListRegistry.resetAfterDragEnd();
  }

  //todo THIS IS WRONG and breaking
  onPositionDrop(event: CdkDragDrop<GroupCompCrewPositionViewModel[]>, grabbedFrom: GroupCompSubgroupViewModel) {
    if(event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);

      event.container.data.forEach((position, index) => {
        position.sortOrder = index;
      })


    } else {
      transferArrayItem(event.previousContainer.data, event.container.data, event.previousIndex, event.currentIndex);

      const current = this.subgroupTreesSubject.getValue();
      const parentIdx = current.findIndex(sub => sub.subgroupId === grabbedFrom.subgroupId);

    }

    this.dropListRegistry.resetAfterDragEnd();
  }

  canDropMember = (drag: CdkDrag) => {
    return drag.data?.memberStatus != undefined;
  }

  canDropSubgroup = (drag: CdkDrag) => {
    return drag.data?.parentSubgroupId !== undefined;
  }

  canDropPosition = (drag: CdkDrag) => {
    return drag.data?.positionId !== undefined;
  }
}
