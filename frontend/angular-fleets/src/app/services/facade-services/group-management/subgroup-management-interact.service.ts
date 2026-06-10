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
import {
  CdkDragDrop,
  CdkDragEnd,
  moveItemInArray,
  transferArrayItem
} from "@angular/cdk/drag-drop";
import {
  GroupCompCrewPositionViewModel
} from "../../../models/group-management-models/view-models/group-composition/group-comp-crew-position-view-model";
import {DropListRegistryService} from "./drop-list-registry.service";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmGenericComponent} from "../../../components/pop-ups/confirm-generic/confirm-generic.component";
import {
  GroupManagementMemberViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {GroupManagementInteractService} from "./group-management-interact.service";
import {
  UpdateSubgroupDropListOrientationRequest
} from "../../../models/group-management-models/request-models/update-subgroup-drop-list-orientation-request";

export interface GroupCompPositionsBrief {
  assigned: number,
  total: number
}

@Injectable()
export class SubgroupManagementInteractService {
  private destroyRef = inject(DestroyRef);

  protected subgroupTreesSubject = new BehaviorSubject<GroupCompSubgroupViewModel[]>([]);
  public subgroupTrees$ = this.subgroupTreesSubject.asObservable();

  protected crewPositionsSubject = new BehaviorSubject<GroupCompCrewPositionViewModel[]>([]);
  public crewPositions$ = this.crewPositionsSubject.asObservable();

  reorientingDropList: boolean = false;

  protected groupPositionsRatio$: BehaviorSubject<GroupCompPositionsBrief> = new BehaviorSubject<GroupCompPositionsBrief>({
    assigned: 0,
    total: 0
  });

  constructor(private compositionApi: GroupCompositionApiService,
              private managementInteract: GroupManagementInteractService,
              private dropListRegistry: DropListRegistryService,
              private dialog: MatDialog) {}

  getExistingGroupComposition (groupId: number) {
    this.compositionApi.getExistingGroupStructure(groupId).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (groupComp: GroupCompositionDto) => {
          this.subgroupTreesSubject.next(groupComp.subgroups ?? []);
          this.crewPositionsSubject.next(groupComp.crewPositions ?? []);
          this.dropListRegistry.pageDataLoading = false;
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

          const newPositions = responseDto.crewPositions;
          const currentPositions = this.crewPositionsSubject.getValue();
          this.crewPositionsSubject.next([
            ...currentPositions,
            ...newPositions
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
              this.getExistingGroupComposition(subgroup.listingId);
              this.managementInteract.fetchActiveRoster(subgroup.listingId);
            })
        }
      })
  }

  updateSubgroupDropListOrientation(subgroup: GroupCompSubgroupViewModel) {
    const request = new UpdateSubgroupDropListOrientationRequest(subgroup);

    this.compositionApi.updateSubgroupDropListOrientation(request).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {})
  }

  onSubgroupDrop(event: CdkDragDrop<GroupCompSubgroupViewModel[]>) {

    const targetContainer = this.dropListRegistry.allSubgroupLists$.getValue()
      .find(list => list.dropList.id === this.dropListRegistry.hoveredTargetId$.getValue())

    // console.log('previous container: ', event.previousContainer.id);
    // console.log('targetContainer: ', targetContainer?.dropList.id);
    // console.log('previdx: ', event.previousIndex, ', currentIdx: ', event.currentIndex);

    if(!targetContainer) {
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
  // is it still? grabbedFrom is unused and probably does not need to be here, its in event already
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

  onMemberDrop(dropData: CdkDragEnd<GroupManagementMemberViewModel>) {
    if (!('memberStatus' in dropData.source.data)) return;
    const {x, y} = dropData.dropPoint;
    const element = document.elementFromPoint(x, y);
    const positionEl = element?.closest('[data-position-id]');
    if (!positionEl) {
      return;
    }

    const rawPositionId = positionEl.getAttribute('data-position-id');

    if(rawPositionId === null) {
      return;
    }

    const targetPositionId = Number(rawPositionId);
    if(Number.isNaN(targetPositionId)) {
      return;
    }

    const position = this.crewPositionsSubject.getValue().find(pos => pos.positionId === targetPositionId);

    if(position) {
      const positionCopy = { ...position, assignedMember: dropData.source.data };

      //TODO RETURN THE UPDATED MEMBER INSTEAD OF GID SO IT CAN BE REASSIGNED TO UPDATE GROUP/ROLE
      this.compositionApi.assignMemberToPosition(positionCopy).pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe((groupId: number) => {
          if(groupId) {
            this.getExistingGroupComposition(groupId);
            this.managementInteract.fetchActiveRoster(groupId);
            this.dropListRegistry.draggedMember$.next(null);
            this.dropListRegistry.hoveredPositionId$.next(null);
          }
        })
    }
  }

  clearPositionAssignment(position: GroupCompCrewPositionViewModel) {
    this.compositionApi.clearMemberPositionAssignment(position).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((groupId: number) => {
        if(groupId) {
          position.assignedMember = null;
          this.getExistingGroupComposition(groupId);
          this.managementInteract.fetchActiveRoster(groupId)
        }
      })
  }

  clearPositionAssignmentByMember() {
    const member = this.managementInteract.selectedMemberSubject$.getValue();

    if(!member) return;

    this.compositionApi.clearPositionAssignmentByMember(member).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (updatedMember: GroupManagementMemberViewModel) => {
        this.managementInteract.findAndReplaceActiveRosterMember(updatedMember);
        this.getExistingGroupComposition(member.listingId);
      }
    });
  }

  clearTrees() {
    this.subgroupTreesSubject.next([]);
  }

}
