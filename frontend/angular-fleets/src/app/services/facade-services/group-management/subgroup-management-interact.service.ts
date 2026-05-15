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
import {CdkDrag, CdkDragDrop, CdkDropList, moveItemInArray, transferArrayItem} from "@angular/cdk/drag-drop";
import {
  GroupCompCrewPositionViewModel
} from "../../../models/group-management-models/view-models/group-composition/group-comp-crew-position-view-model";
import {DropListRegistryService} from "./drop-list-registry.service";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmGenericComponent} from "../../../components/pop-ups/confirm-generic/confirm-generic.component";

export interface GroupCompPositionsBrief {
  assigned: number,
  total: number
}

@Injectable({
  providedIn: 'root'
})
export class SubgroupManagementInteractService {
  private destroyRef = inject(DestroyRef);
  public isDragging = false;

  protected subgroupTreesSubject = new BehaviorSubject<GroupCompSubgroupViewModel[]>([]);
  public subgroupTrees$ = this.subgroupTreesSubject.asObservable();

  protected groupPositionsRatio$: BehaviorSubject<GroupCompPositionsBrief> = new BehaviorSubject<GroupCompPositionsBrief>({
    assigned: 0,
    total: 0
  });

  constructor(private compositionApi: GroupCompositionApiService,
              private dropListRegistry: DropListRegistryService,
              private dialog: MatDialog) {}

  getExistingSubgroupTrees(groupId: number) {
    this.compositionApi.getExistingGroupStructure(groupId).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (subgroupTrees: GroupCompositionDto) => {
          this.subgroupTreesSubject.next(subgroupTrees.subgroups);
        }
      })
  }

  calculatePositionsRatio() {

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

  deleteSubgroup(subgroup: GroupCompSubgroupViewModel) {
    const dialogRef = this.dialog.open(ConfirmGenericComponent, {
      data: {
        message: 'Delete this subgroup and all of its structurally nested contents? Any group members assigned ' +
          'to this group will have their position assignment reset.',
        title: 'Subgroup: \"' + subgroup.subgroupLabel + '\", and its contents.',
      }
    });

    dialogRef.afterClosed().pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(result => {
        if(result) {
          this.compositionApi.deleteSubgroup(subgroup.listingId, subgroup.subgroupId).pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe(() => {
              const current = this.subgroupTreesSubject.getValue();
              const idx = current.findIndex(sub => sub.subgroupId === subgroup.subgroupId);
              if(idx > -1) {
                this.subgroupTreesSubject.next([
                  ...current.slice(0, idx),
                  ...current.slice(idx + 1)
                ])
              }
            })
        }
      })
  }

  onSubgroupDrop(event: CdkDragDrop<GroupCompSubgroupViewModel[]>) {

    const targetContainer = this.dropListRegistry.droppableSubgroupLists
      .find(list => list.dropList.id === this.dropListRegistry.hoveredTargetId$.getValue())

    console.log('previous container: ', event.previousContainer.id);
    console.log('targetContainer: ', targetContainer?.dropList.id);
    console.log('previdx: ', event.previousIndex, ', currentIdx: ', event.currentIndex);

    if(!targetContainer) {
      console.log('catching?');
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);

      event.container.data.forEach((subgroup, index) => {
        subgroup.sortOrder = index;
      })
    } else {
      transferArrayItem(event.previousContainer.data, targetContainer?.dropList.data, event.previousIndex, event.currentIndex);

      targetContainer?.dropList.data.forEach((subgroup: GroupCompSubgroupViewModel, index: number) => {
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

  clearTrees() {
    this.subgroupTreesSubject.next([]);
  }

}
