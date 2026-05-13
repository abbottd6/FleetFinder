import {DestroyRef, ElementRef, inject, Injectable} from '@angular/core';
import {CdkDrag, CdkDragDrop, CdkDragEnd, CdkDragMove, CdkDropList} from "@angular/cdk/drag-drop";
import {BehaviorSubject, debounceTime, EMPTY, Subject, switchMap, take} from "rxjs";
import {environment} from "../../../../environments/environment";
import {
  GroupCompSubgroupViewModel
} from "../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";
import {
  GroupCompCrewPositionViewModel
} from "../../../models/group-management-models/view-models/group-composition/group-comp-crew-position-view-model";
import {
  GroupManagementMemberViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {MouseEventService} from "../mouse-event.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

export type DropListEntityType = 'subgroup' | 'position' | 'member' | 'root';

export interface DropListRegistration {
  id: string;
  dropList: CdkDropList;
  entityType: DropListEntityType;
  element: ElementRef<HTMLElement>;
  treeDepth: number;
  parentId?: string;
}

export interface ElementContainerRegistration {
  id: string;
  entityType: DropListEntityType;
  dropLists: CdkDropList[];
  element: ElementRef<HTMLElement>;
  treeDepth: number;
  parentId?: string;
}

type DropData = GroupCompSubgroupViewModel | GroupCompCrewPositionViewModel | GroupManagementMemberViewModel

type DropPredicate = (
  dropData: DropData,
  dropTarget: DropListRegistration
) => boolean;

const DROP_COMPATIBILITY_PREDICATES: Record< string, DropPredicate> = {
  SUBGROUP_TO_SUBGROUP: (dragData, target) =>
    'parentSubgroupId' in dragData && (target.entityType === 'subgroup'),

  POSITION_TO_SUBGROUP_POSITIONS: (dragData, target) =>
    'assignedMember' in dragData && (target.entityType === 'position'),

  MEMBER_TO_POSITION: (dragData, target) =>
    'memberStatus' in dragData && (target.entityType === 'position'),
}

@Injectable({
  providedIn: 'root'
})
export class DropListRegistryService {
  private destroyRef = inject(DestroyRef)

  private droppableSubgroupLists: DropListRegistration[] = [];
  public allSubgroupLists$: BehaviorSubject<CdkDropList[]> = new BehaviorSubject<CdkDropList[]>([]);

  private droppablePositionLists: DropListRegistration[] = [];
  public allPositionLists$: BehaviorSubject<CdkDropList[]> = new BehaviorSubject<CdkDropList[]>([]);

  private registeredContainers: ElementContainerRegistration[] = [];

  public dragMoved$ = new Subject<CdkDragMove<any> | null>();
  public validDropTargetType$: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);
  public isDragging$ = new BehaviorSubject<boolean>(false);

  public hoveredList$ = new BehaviorSubject<DropListRegistration | null>(null);
  public hoveredContainer$ = new BehaviorSubject<ElementContainerRegistration | null>(null);

  constructor(private mouseService: MouseEventService) {
    this.dragMoved$.pipe(
      takeUntilDestroyed(this.destroyRef),
      debounceTime(120),
    ).subscribe(event => {
      const point = event?.pointerPosition;

      if(!point) return;

      this.validDropTargetType$.next('assignedMember' in event.source.data ? 'position' : 'subgroup');

      const hoveredList = this.findDeepestListByProximity(point.x, point.y);
      const hoveredContainer = this.findDeepestContainerByProximity(point.x, point.y);

      this.hoveredList$.next(hoveredList ?? null);
      this.hoveredContainer$.next(hoveredContainer ?? null);
      // console.log(hoveredContainer);
    })

    this.isDragging$.pipe(
      switchMap(isDragging => isDragging ? this.mouseService.mouseUp$ : EMPTY),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(event => {
      this.resetAfterDragEnd();
    })
  }

  registerList(
    id: string,
    entityType: DropListEntityType,
    dropList: CdkDropList,
    element: ElementRef<HTMLElement>,
    treeDepth: number,
    parentId?: string): DropListRegistration {

    switch (entityType) {
      case 'position': {
        const idx = this.droppablePositionLists.push({
          id,
          dropList,
          entityType,
          element,
          treeDepth,
          parentId
        }) - 1;
        this.allPositionLists$.next(this.droppablePositionLists.map(reg => reg.dropList));
        return this.droppablePositionLists[idx];
      }
      default: {
        const idx = this.droppableSubgroupLists.push({
          id,
          dropList,
          entityType,
          element,
          treeDepth,
          parentId
        }) - 1;
        this.allSubgroupLists$.next(this.droppableSubgroupLists
          // .filter(reg => reg.entityType !== 'root')
          .map(reg => reg.dropList));
        return this.droppableSubgroupLists[idx];
      }
    }
  }

  unregisterList(unregister: DropListRegistration) {

    switch (unregister.entityType) {
      case 'subgroup': {
        const idx = this.droppableSubgroupLists.indexOf(unregister);
        if(idx > -1) {
          this.droppableSubgroupLists.splice(idx, 1);
        }
        break;
      }
      default: {
        const idx = this.droppablePositionLists.indexOf(unregister);
        if(idx > -1) {
          this.droppablePositionLists.splice(idx, 1);
        }
        break;
      }
    }

    // this.refreshConnections();
  }

  registerContainer(
    id: string,
    entityType: DropListEntityType,
    dropLists: CdkDropList[],
    element: ElementRef<HTMLElement>,
    treeDepth: number,
    parentId?: string,
  ){
    const idx = this.registeredContainers.push({
      id,
      entityType,
      dropLists,
      element,
      treeDepth,
      parentId
    }) - 1;

    return this.registeredContainers[idx];
  }

  unregisterContainer(unregister: ElementContainerRegistration) {
    const idx = this.registeredContainers.indexOf(unregister);
    if(idx > -1) {
      this.registeredContainers.splice(idx, 1);
    }
  }

  private hoverListCandidates: DropListRegistration[] = []
  private hoverListCandidateIdx = 0;
  private holdTimer: ReturnType<typeof setTimeout> | null = null;

  private findDeepestContainerByProximity(x: number, y: number) {
    const candidates = this.registeredContainers
      .filter(container => {
        const rect = container.element.nativeElement.getBoundingClientRect();

        return (
          x >= rect.left &&
          x <= rect.right &&
          y >= rect.top &&
          y <= rect.bottom
        );
      })
      .sort((a, b) => {
        const areaA = a.element.nativeElement.offsetWidth * a.element.nativeElement.offsetHeight;
        const areaB = b.element.nativeElement.offsetWidth * b.element.nativeElement.offsetHeight;

        return areaB - areaA;
      });

    return candidates[this.hoverListCandidateIdx] ?? candidates[0];
  }

  private findDeepestListByProximity(x: number, y: number) {
    const candidates = this.droppableSubgroupLists
      .filter(list => {
        const rect = list.element.nativeElement.getBoundingClientRect();

        return (
          x >= rect.left &&
          x <= rect.right &&
          y >= rect.top &&
          y <= rect.bottom
        );
      })
      .sort((a, b) => {
        const areaA = a.element.nativeElement.offsetWidth * a.element.nativeElement.offsetHeight;

        const areaB = b.element.nativeElement.offsetWidth * b.element.nativeElement.offsetHeight;

        return areaA - areaB;
      });

    if (JSON.stringify(candidates.map(c => c.id)) !== JSON.stringify(this.hoverListCandidates.map(c => c.id))) {
      this.hoverListCandidates = candidates;
      this.hoverListCandidateIdx = 0;
      clearTimeout(this.holdTimer!);
      this.scheduleIndexIncrement();
    }

    if(this.hoverListCandidateIdx > candidates.length) {
      this.hoverListCandidateIdx = 0;
    }

    return this.hoverListCandidates[this.hoverListCandidateIdx] ?? candidates[0];
  }

  private scheduleIndexIncrement() {
    this.holdTimer = setTimeout (() => {
      if (this.hoverListCandidateIdx < this.hoverListCandidates.length - 1) {
        this.hoverListCandidateIdx++;
        this.scheduleIndexIncrement();
      }
    }, 1500);
  }

  public onDragMoved(event: CdkDragMove<any>): void {
    this.isDragging$.next(true)
    this.dragMoved$.next(event);
  }


  public resetAfterDragEnd() {
    this.isDragging$.next(false);
    this.hoveredContainer$.next(null);
    this.hoveredList$.next(null);
    this.dragMoved$.next(null);
    this.hoverListCandidateIdx = 0;
    this.hoverListCandidates = [];
    clearTimeout(this.holdTimer!);
  }

  public isCompatibleDrop = (dragData: CdkDrag, dropList: CdkDropList): boolean => {
    // console.log('dragData: ', dragData.data);
    // console.log('dropList: ', dropList.data);

    let registration = this.droppableSubgroupLists.find(list => list.dropList === dropList);

    const elementData = dragData.data as DropData;

    console.log(elementData);

    if(!registration) {
      registration = this.droppablePositionLists.find(list => list.dropList === dropList);
    }

    if(!registration) {
      console.log('not registration');
      return false;
    }
    return Object.values(DROP_COMPATIBILITY_PREDICATES).some(predicate => predicate(elementData, registration));
  }

  canEnterParent = (drag: CdkDrag, drop: CdkDropList) => {
    return
  }
}
