import {DestroyRef, ElementRef, inject, Injectable} from '@angular/core';
import {CdkDrag, CdkDragMove, CdkDropList} from "@angular/cdk/drag-drop";
import {BehaviorSubject, debounceTime, EMPTY, filter, Subject, switchMap} from "rxjs";

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

export interface SubgroupHoverTargetRegistration {
  id: string;
  dropList: CdkDropList;
  element: ElementRef<HTMLElement>;
  containerEl: ElementContainerRegistration;
  treeDepth: number;
}

export type DropData = GroupCompSubgroupViewModel | GroupCompCrewPositionViewModel | GroupManagementMemberViewModel

type DropPredicate = (
  dropData: DropData,
  dropTarget: DropListRegistration
) => boolean;

export const DROP_COMPATIBILITY_PREDICATES: Record< string, DropPredicate> = {
  SUBGROUP_TO_SUBGROUP: (dragData, target) =>
    ('parentSubgroupId' in dragData) && (target.entityType === 'subgroup'),

  POSITION_TO_SUBGROUP_POSITIONS: (dragData, target) =>
    'assignedMember' in dragData && (target.entityType === 'position'),

  MEMBER_TO_POSITION: (dragData, target) =>
    ('memberStatus' in dragData) && (target.entityType === 'position'),
}

@Injectable({
  providedIn: 'root'
})
export class DropListRegistryService {
  private destroyRef = inject(DestroyRef)

  public droppableSubgroupLists: DropListRegistration[] = [];
  public allSubgroupLists$: BehaviorSubject<CdkDropList[]> = new BehaviorSubject<CdkDropList[]>([]);

  public droppablePositionLists: DropListRegistration[] = [];
  public allPositionLists$: BehaviorSubject<CdkDropList[]> = new BehaviorSubject<CdkDropList[]>([]);

  private hoverTargetList: SubgroupHoverTargetRegistration[] = [];
  public hoveredTargetId$ = new BehaviorSubject<string | null>(null);
  private hoverTargetTimer: ReturnType<typeof setTimeout> | null = null;
  public targetBoundingContainer$ = new BehaviorSubject<ElementContainerRegistration | null>(null);

  private registeredContainers: ElementContainerRegistration[] = [];

  public dragMoved$ = new Subject<CdkDragMove<any> | null>();
  public validDropTargetType$: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);
  public isDragging$ = new BehaviorSubject<boolean>(false);

  public hoveredList$ = new BehaviorSubject<DropListRegistration | null>(null);
  public hoveredContainer$ = new BehaviorSubject<ElementContainerRegistration | null>(null);

  constructor(private mouseService: MouseEventService) {

    this.dragMoved$.pipe(
      takeUntilDestroyed(this.destroyRef),
      filter(move => !!move),
      debounceTime(120),
    ).subscribe(event => {
      const point = event.pointerPosition;
      const handle = this.findHandleAtPoint(point.x, point.y);

      if(!point) return;

      this.validDropTargetType$.next('assignedMember' in event.source.data ? 'position' : 'subgroup');
      //
      console.log('move target handle: ', handle?.id);
      // console.log('handleContainerEl: ', handle?.containerEl.id)
      console.log('hoveredTargetId: ', this.hoveredTargetId$.getValue());
      console.log('targetBoundingContainer: ', this.targetBoundingContainer$.getValue()?.id);
      // console.log('state: ', handle?.dropList.disabled)

      if(handle?.dropList.id === this.hoveredTargetId$.getValue()) return;

      clearTimeout(this.hoverTargetTimer!);

      const inContainer: boolean = this.pointWithinBoundingContainer(point.x, point.y);

      if(!handle) {
        console.log('no handle, inContainer:', inContainer, 'bounds:', this.targetBoundingContainer$.getValue()?.element.nativeElement.getBoundingClientRect());
        if(!inContainer) {
          this.hoveredTargetId$.next(null);
          this.targetBoundingContainer$.next(null);
        }
        return;
      }

      this.hoverTargetTimer = setTimeout(() => {
        this.hoveredTargetId$.next(handle.dropList.id);
        this.targetBoundingContainer$.next(handle.containerEl);
        handle.dropList._dropListRef.disabled = false;
        handle.dropList._dropListRef.sortingDisabled = false;
        handle.dropList._dropListRef._startReceiving(event.source.dropContainer._dropListRef, event.source._dragRef as any);
        handle.dropList._dropListRef.enter(event.source._dragRef, event.pointerPosition.x, event.pointerPosition.y);
        handle.dropList._dropListRef._sortItem(event.source._dragRef, point.x, point.y, event.delta)
        handle.dropList._dropListRef.exit(event.source._dragRef);

      }, 400)

    })

    this.isDragging$.pipe(
      switchMap(isDragging => isDragging ? this.mouseService.mouseUp$ : EMPTY),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(event => {
      this.resetAfterDragEnd();
    })
  }

  findHandleAtPoint(x: number, y: number) {
    return this.hoverTargetList.find(target => {
      const rect = target.element.nativeElement.getBoundingClientRect();
      return (
        x >= rect.left &&
        x <= rect.right &&
        y >= rect.top &&
        y <= rect.bottom
      );
    })
  }

  pointWithinBoundingContainer(x: number, y: number) {
    const currentBounds = this.targetBoundingContainer$.getValue();

    if(!currentBounds) return false;

    const rect = currentBounds.element.nativeElement.getBoundingClientRect();
    return (
      x >= rect.left &&
      x <= rect.right &&
      y >= rect.top &&
      y <= rect.bottom
    )
  }

  public onDragMoved(event: CdkDragMove<any>): void {
    this.isDragging$.next(true)
    this.dragMoved$.next(event);
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

        const list = this.droppablePositionLists.find(p => p.id === id);

        if(!list) {
          throw new Error(`Position list registration for id: ${id} not created/could not be found.`)
        }
        return this.droppablePositionLists[idx];
      }
      default: {
        this.droppableSubgroupLists.push({
          id,
          dropList,
          entityType,
          element,
          treeDepth,
          parentId
        });

        this.allSubgroupLists$.next(this.droppableSubgroupLists
          .map(reg => reg.dropList));

        const list = this.droppableSubgroupLists.find(sub => sub.id === id);

        if(!list) {
          throw new Error(`Subgroup list registration for id: ${id} not created/could not be found.`)
        }

        return list;
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
  }

  registerHoverTarget(
    id: string,
    dropList: CdkDropList,
    element: ElementRef<HTMLElement>,
    containerEl: ElementContainerRegistration,
    treeDepth: number,
  ) {
    this.hoverTargetList.push({
      id,
      dropList,
      element,
      containerEl,
      treeDepth,
    });

    const target = this.hoverTargetList.find(t => t.id === id);

    if(!target) {
      throw new Error(`HoverTargetRegistration with id: ${id} not created/could not be found.`)
    }

    return target;
  }

  registerContainer(
    id: string,
    entityType: DropListEntityType,
    dropLists: CdkDropList[],
    element: ElementRef<HTMLElement>,
    treeDepth: number,
    parentId?: string,
  ){
    this.registeredContainers.push({
      id,
      entityType,
      dropLists,
      element,
      treeDepth,
      parentId
    });

    const container = this.registeredContainers.find(c => c.id === id);

    if(!container) {
      throw new Error(`ElementContainer registration for id: ${id} not created/could not be found.`)
    }

    return container;
  }

  unregisterContainer(unregister: ElementContainerRegistration) {
    const idx = this.registeredContainers.indexOf(unregister);
    if(idx > -1) {
      this.registeredContainers.splice(idx, 1);
    }
  }

  // private lastMoveEventElements: Element[] = []
  // private lastMoveIdx = 0;
  // private holdTimer: ReturnType<typeof setTimeout> | null = null;
  //
  // private findDeepestContainerByProximity(x: number, y: number) {
  //   const elements = document.elementsFromPoint(x, y);
  //
  //   return elements.map(el => this.registeredContainers.find(c => c.element.nativeElement === el))
  //     .find(c => c !== undefined);
  //
  //   // const candidates = this.registeredContainers
  //   //   .filter(container => {
  //   //     const rect = container.element.nativeElement.getBoundingClientRect();
  //   //
  //   //     return (
  //   //       x >= rect.left &&
  //   //       x <= rect.right &&
  //   //       y >= rect.top &&
  //   //       y <= rect.bottom
  //   //     );
  //   //   })
  //   //   .sort((a, b) => {
  //   //     const areaA = a.element.nativeElement.offsetWidth * a.element.nativeElement.offsetHeight;
  //   //     const areaB = b.element.nativeElement.offsetWidth * b.element.nativeElement.offsetHeight;
  //   //
  //   //     return areaB - areaA;
  //   //   });
  //   //
  //   // return candidates[this.hoverListCandidateIdx] ?? candidates[0];
  // }
  //
  // private findDeepestListByProximity(x: number, y: number, dragEvent: CdkDrag) {
  //   const elements = document.elementsFromPoint(x, y);
  //
  //   console.log('ele length: ' + elements.length);
  //
  //   if (JSON.stringify(elements.map(el => el.id)) !== JSON.stringify(this.lastMoveEventElements.map(hist => hist.id))) {
  //     this.lastMoveEventElements = elements
  //     this.lastMoveIdx = 0;
  //     clearTimeout(this.holdTimer!);
  //     this.scheduleIndexIncrement(dragEvent);
  //   }
  //
  //   const result = elements.slice(this.lastMoveIdx).map(el => this.droppableSubgroupLists.find(list => list.element.nativeElement === el))
  //     .find(list => list  !== undefined);
  //
  //   console.log('slice and map: ' + result?.id);
  //
  //   return result;
  // }
  //
  //
  // private scheduleIndexIncrement(dragEvent: CdkDrag) {
  //   const endOfElements = this.lastMoveIdx >= this.lastMoveEventElements.length - 1;
  //   this.holdTimer = setTimeout (() => {
  //     if(!endOfElements) {
  //       this.lastMoveIdx++;
  //       this.scheduleIndexIncrement(dragEvent);
  //     }
  //   }, 1200);
  // }

  public isCompatibleDrop = (dragData: CdkDrag, dropList: CdkDropList): boolean => {

    let registration = this.droppableSubgroupLists.find(
      list => list.dropList === dropList
    );

    if(!registration) {
      console.log('no subgroup registration.')
      registration = this.droppablePositionLists.find(list => list.dropList === dropList);
    }

    if(!registration) {
      console.log('no position registration.')
      return false;
    }

    // const currentHoveredTargetId = this.hoveredTargetId$.getValue();
    const elementData = dragData.data as DropData;

    // if(!currentHoveredTargetId) {
    //   console.log('no hovered target id');
    //   return false;
    // }

    const droppable = Object.values(DROP_COMPATIBILITY_PREDICATES)
      .some(predicate => predicate(elementData, registration));

    console.log('droppable: ', droppable);

    return droppable
  }

  public resetAfterDragEnd() {
    this.isDragging$.next(false);
    this.hoveredContainer$.next(null);
    this.hoveredList$.next(null);
    this.dragMoved$.next(null);
    this.hoveredTargetId$.next(null);
    this.targetBoundingContainer$.next(null);
    clearTimeout(this.hoverTargetTimer!);
  }
}
