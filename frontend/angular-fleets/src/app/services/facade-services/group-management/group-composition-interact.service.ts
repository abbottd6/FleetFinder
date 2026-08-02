import {DestroyRef, inject, Injectable} from '@angular/core';
import {
  GroupCompositionApiService
} from "../../api-services/group-management/group-composition-api/group-composition-api.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {
  CrewTemplateViewModel
} from "../../../models/group-management-models/view-models/group-composition/crew-template-view-model";
import {BehaviorSubject, catchError, EMPTY, throwError} from "rxjs";
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
import {
  GroupManagementMemberViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {GroupManagementInteractService} from "./group-management-interact.service";
import {
  UpdateSubgroupDropListOrientationRequest
} from "../../../models/group-management-models/request-models/update-subgroup-drop-list-orientation-request";
import {HttpErrorResponse} from "@angular/common/http";
import {BoundedHistoryStack} from "../../../models/bounded-history-stack";
import {
  CreateOrEditPositionPopupComponent
} from "../../../components/group-management-page/subgroup-management/create-or-edit-position-popup/create-or-edit-position-popup.component";
import {NewOrEditPositionRequest} from "../../../models/group-management-models/request-models/new-or-edit-position-request";

export interface GroupCompPositionsRatio {
  assigned: number,
  total: number
}

export interface SubgroupHistoryElement {
  actionLabel: string,
  tree: GroupCompSubgroupViewModel[]
}

@Injectable()
export class GroupCompositionInteractService {
  private destroyRef = inject(DestroyRef);

  protected groupId: number | undefined;

  protected subgroupTreesSubject = new BehaviorSubject<GroupCompSubgroupViewModel[]>([]);
  public subgroupTrees$ = this.subgroupTreesSubject.asObservable();

  private readonly stackSize = 20;
  private subgroupHistoryCache = new BoundedHistoryStack<SubgroupHistoryElement>(this.stackSize);

  protected crewPositionMap = new Map<number, GroupCompCrewPositionViewModel>();

  reorientingDropList: boolean = false;

  public groupPositionsRatio$: BehaviorSubject<GroupCompPositionsRatio> = new BehaviorSubject<GroupCompPositionsRatio>({
    assigned: 0,
    total: 0
  });

  constructor(private compositionApi: GroupCompositionApiService,
              private managementInteract: GroupManagementInteractService,
              private dropListRegistry: DropListRegistryService,
              private dialog: MatDialog) {
    this.groupId = this.managementInteract.sessionManager?.listing?.groupId;
  }

  getExistingGroupComposition() {
    if(!this.groupId) return;

    this.compositionApi.getExistingGroupStructure(this.groupId).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (groupComp: GroupCompositionDto) => {
          this.subgroupTreesSubject.next(groupComp.subgroups ?? []);
          this.cleanAndBuildPositionMap();
          this.dropListRegistry.pageDataLoading = false;
        }
      })
  }

  cleanAndBuildPositionMap() {
    this.crewPositionMap.clear();
    let assigned = 0;

    const walk = (subgroups: GroupCompSubgroupViewModel[]) => {
      for (const node of subgroups) {
        node.crewPositions?.forEach(p => {
          this.crewPositionMap.set(p.positionId, p);
          if(p.assignedMember != null) assigned++;
        });
        walk(node.subgroups ?? []);
      }
    };
    walk(this.subgroupTreesSubject.getValue());

    this.groupPositionsRatio$.next({ assigned: assigned, total: this.crewPositionMap.size });
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

          this.cleanAndBuildPositionMap();
          // const newPositions = responseDto.crewPositions;
          // const currentPositions = this.crewPositionsSubject.getValue();
          // this.crewPositionsSubject.next([
          //   ...currentPositions,
          //   ...newPositions
          // ])
        }
      });
  }

  persistState() {
    this.subgroupTreesSubject.next([...this.subgroupTreesSubject.value])



    if(!this.groupId) {
      console.log('not group id')
      return EMPTY;
    }

    const latest = new GroupCompositionDto(
      this.groupId,
      this.subgroupTreesSubject.getValue(),
    );

    this.cleanAndBuildPositionMap();

    return this.compositionApi.updateGroupCompositionState(latest).pipe(
      catchError((err: HttpErrorResponse)=> {
        console.log(err.message);
        this.managementInteract.showSnackBarMessage('Error persisting changes.');
        return EMPTY;
      })
    )
  }

  deleteCrewPosition(posForDelete: GroupCompCrewPositionViewModel) {
    const groupId = posForDelete.groupId;
    const positionId = posForDelete.positionId;

    this.compositionApi.softDeletePosition(groupId, positionId).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.cleanAndBuildPositionMap();
          this.managementInteract.fetchActiveRoster(groupId);
        }
      })
  }

  createNewPosition(parentSubgroup: GroupCompSubgroupViewModel) {
    const dialogRef = this.dialog.open(CreateOrEditPositionPopupComponent, {
      data: {
        groupId: parentSubgroup.listingId,
        subgroup: parentSubgroup,
        position: null
      }
    })

    const actionLabel = 'New Position'
    this.pushSubgroupActionToHistoryCache(actionLabel);

    dialogRef.afterClosed().pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((newDto: NewOrEditPositionRequest) => {
        if(newDto) {
          this.compositionApi.createNewPosition(newDto).pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe((newPosition: GroupCompCrewPositionViewModel) => {
              parentSubgroup.crewPositions = [
                ...parentSubgroup.crewPositions,
                newPosition
              ]

              this.subgroupTreesSubject.next([...this.subgroupTreesSubject.value]);
              this.cleanAndBuildPositionMap();
            })
        }
      })
  }

  editPosition(parent: GroupCompSubgroupViewModel, toEdit: GroupCompCrewPositionViewModel) {
    const dialogRef = this.dialog.open(CreateOrEditPositionPopupComponent, {
      data: {
        groupId: toEdit.groupId,
        subgroup: null,
        position: toEdit
      }
    })

    dialogRef.afterClosed().pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((editDto: NewOrEditPositionRequest) => {
        if(editDto) {
          this.compositionApi.editExistingPosition(editDto, toEdit.positionId).pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe((updated: GroupCompCrewPositionViewModel)=> {
              const old = parent.crewPositions;
              const idx = old.findIndex(p => p.positionId === toEdit.positionId);

              parent.crewPositions = [
                ...old.slice(0, idx),
                updated,
                ...old.slice(idx + 1)
              ];

              this.subgroupTreesSubject.next([...this.subgroupTreesSubject.value]);
              this.cleanAndBuildPositionMap();

              if(updated.assignedMember) {
                this.managementInteract.fetchActiveRoster(this.managementInteract.groupId);
              }
            })
        }
      });
  }

  deleteRootLevelSubgroup(forDelete: GroupCompSubgroupViewModel) {
    const current = this.subgroupTreesSubject.getValue();

    if(current.length === 1) {
      this.subgroupTreesSubject.next([]);
    } else {
      const idx = current.findIndex(sub => sub.subgroupId === forDelete.subgroupId);

      this.subgroupTreesSubject.next([
        ...current.slice(0, idx),
        ...current.slice(idx + 1)
      ])
    }

    this.persistState().pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.cleanAndBuildPositionMap();
          this.managementInteract.fetchActiveRoster(this.managementInteract.groupId);
        }
      });
  }

  pushSubgroupActionToHistoryCache(actionLabel: string) {
    const historyEl: SubgroupHistoryElement = {
      actionLabel: actionLabel,
      tree: structuredClone(this.subgroupTreesSubject.value)
    }

    this.subgroupHistoryCache.push(structuredClone(historyEl));
  }

  get undoIsDisabled(): boolean {
    return !this.subgroupHistoryCache.canUndo;
  }

  get peekAtLastActionLabel(): string | undefined {
    return this.subgroupHistoryCache.peek()?.actionLabel;
  }

  undoLastSubgroupAction(): void {
    const previousState = this.subgroupHistoryCache.pop();
    if (!previousState) return;

    this.subgroupTreesSubject.next(previousState.tree);

    this.persistState().pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.managementInteract.fetchActiveRoster(this.managementInteract.groupId);
        }
      })
  }

  updateSubgroupDropListOrientation(subgroup: GroupCompSubgroupViewModel) {
    const request = new UpdateSubgroupDropListOrientationRequest(subgroup);

    this.compositionApi.updateSubgroupDropListOrientation(request).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {})
  }

  updateSubgroupLabel(subgroupId: number, newLabel: string) {
    this.compositionApi.updateSubgroupLabel(subgroupId, newLabel).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        error: () => {
          this.managementInteract.showSnackBarMessage('There was an error updating this label.')
        }
      })
  }

  onSubgroupDrop(event: CdkDragDrop<GroupCompSubgroupViewModel[]>) {
    const actionLabel = 'Move Subgroup';
    this.pushSubgroupActionToHistoryCache(actionLabel);

    const targetContainer = this.dropListRegistry.allSubgroupLists$.getValue()
      .find(list => list.dropList.id === this.dropListRegistry.hoveredTargetId$.getValue())

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

    this.persistState().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: () => {
        this.getExistingGroupComposition();
      }
    });

    this.dropListRegistry.resetAfterDragEnd();
  }

  onPositionDrop(event: CdkDragDrop<GroupCompCrewPositionViewModel[]>, grabbedFrom: GroupCompSubgroupViewModel) {
    const actionLabel = 'Move Crew Position';
    this.pushSubgroupActionToHistoryCache(actionLabel);

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

    this.persistState().pipe(takeUntilDestroyed(this.destroyRef)).subscribe();

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

    const position = this.crewPositionMap.get(targetPositionId);


    if(position) {
      const actionLabel = 'Assign Member';
      this.pushSubgroupActionToHistoryCache(actionLabel);

      const previouslyAssignedId = dropData.source.data.memberPosition?.positionId;

      if(previouslyAssignedId) {
        const old = this.crewPositionMap.get(previouslyAssignedId);
        if(old != null) {
          old.assignedMember = null;
        }
      }

      position.assignedMember = dropData.source.data;
      this.subgroupTreesSubject.next([...this.subgroupTreesSubject.getValue()]);
      this.cleanAndBuildPositionMap();

      this.compositionApi.assignMemberToPosition(position).pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe((groupId: number) => {
          if(groupId) {
            this.getExistingGroupComposition();
            this.managementInteract.fetchActiveRoster(groupId);
            this.dropListRegistry.draggedMember$.next(null);
            this.dropListRegistry.hoveredPositionId$.next(null);
          }
        })
    }
  }

  clearPositionAssignment(position: GroupCompCrewPositionViewModel) {
    const actionLabel = 'Unassign Member';
    this.pushSubgroupActionToHistoryCache(actionLabel);

    this.compositionApi.clearMemberPositionAssignment(position).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((groupId: number) => {
        if(groupId) {
          position.assignedMember = null;
          this.getExistingGroupComposition();
          this.managementInteract.fetchActiveRoster(groupId)
        }
      })
  }

  clearPositionAssignmentByMember() {
    const member = this.managementInteract.selectedMemberSubject$.getValue();

    if(!member) return;

    const actionLabel = 'Unassign Member';
    this.pushSubgroupActionToHistoryCache(actionLabel);

    this.compositionApi.clearPositionAssignmentByMember(member).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (updatedMember: GroupManagementMemberViewModel) => {
        this.managementInteract.findAndReplaceActiveRosterMember(updatedMember);
        this.getExistingGroupComposition();
      }
    });
  }

  removeGroupMember() {
    const selectedMember = this.managementInteract.selectedMemberSubject$.getValue();

    if(!selectedMember) return;

    const memberRemoved = this.managementInteract.openRemoveMemberPopup(selectedMember);

    if(memberRemoved) {
      this.getExistingGroupComposition();
    }
  }

  clearTrees() {
    this.subgroupTreesSubject.next([]);
  }

}
