import {ChangeDetectorRef, DestroyRef, ElementRef, inject, Injectable, NgZone} from '@angular/core';
import {CdkDrag, CdkDragDrop, CdkDragMove, CdkDropList} from "@angular/cdk/drag-drop";
import {
  BehaviorSubject,
  debounceTime,
  distinctUntilChanged,
  EMPTY,
  filter,
  Subject,
  switchMap
} from "rxjs";

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
import {tap} from "rxjs/operators";

export type DropListEntityType = 'subgroup' | 'position' | 'member' | 'root' | 'invalid';

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
  dropTarget: DropListRegistration,
  boundingContainer: ElementContainerRegistration | null
) => boolean;

export const DROP_COMPATIBILITY_PREDICATES: Record< string, DropPredicate> = {
  SUBGROUP_TO_SUBGROUP: (dropData, target, boundingContainer) => {
    const idx = boundingContainer?.dropLists.findIndex(dl => dl.id === target.dropList.id);
    return ('parentSubgroupId' in dropData) && (target.entityType === 'subgroup') && (!!idx && idx > -1);
},

  SUBGROUP_TO_ROOT: (dropData, target, boundingContainer) => {
    const idx = boundingContainer?.dropLists.findIndex(dl => dl.id === target.dropList.id);
    return ('parentSubgroupId' in dropData) && (target.entityType === 'root') && (!!idx && idx > -1);
  },

  POSITION_TO_SUBGROUP_POSITIONS: (dropData, target) =>
    'positionId' in dropData && (target.entityType === 'position'),
}

export function getDropEntityType(dropData: DropData): DropListEntityType {
  if('parentSubgroupId' in dropData) return 'subgroup';
  if('positionId' in dropData) return 'position';
  if('memberStatus' in dropData) return 'member';
  return 'invalid';
}

@Injectable()
export class DropListRegistryService {
  private destroyRef = inject(DestroyRef)

  public pageDataLoading: boolean = false;

  private droppableSubgroupLists: DropListRegistration[] = [];
  public allSubgroupLists$: BehaviorSubject<DropListRegistration[]> = new BehaviorSubject<DropListRegistration[]>([]);

  private droppablePositionLists: DropListRegistration[] = [];
  public allPositionLists$: BehaviorSubject<DropListRegistration[]> = new BehaviorSubject<DropListRegistration[]>([]);

  private hoverTargetList: SubgroupHoverTargetRegistration[] = [];
  public hoveredTargetId$ = new BehaviorSubject<string | null>(null);
  private hoverTargetTimer: ReturnType<typeof setTimeout> | null = null;
  public targetBoundingContainer$ = new BehaviorSubject<ElementContainerRegistration | null>(null);

  private registeredContainers: ElementContainerRegistration[] = [];

  public dragMoved$ = new Subject<CdkDragMove<any> | null>();
  public dropDataType$: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);
  public isDragging$ = new BehaviorSubject<boolean>(false);

  public hoveredList$ = new BehaviorSubject<DropListRegistration | null>(null);

  public hoveredPositionId$: BehaviorSubject<number | null> = new BehaviorSubject<number | null>(null);
  public draggedMember$: BehaviorSubject<GroupManagementMemberViewModel | null> = new BehaviorSubject<GroupManagementMemberViewModel | null>(null);

  constructor(private mouseService: MouseEventService,
              private ngZone: NgZone,
              private cdr: ChangeDetectorRef) {

    this.dragMoved$.pipe(
      takeUntilDestroyed(this.destroyRef),
      filter(move => !!move),
      debounceTime(120),
    ).subscribe(event => {
      const point = event.pointerPosition;
      const handle = this.findHandleAtPoint(point.x, point.y);

      if(!point) return;

      this.dropDataType$.next(getDropEntityType(event.source.data));

      console.log('dropDataType$: ', this.dropDataType$.getValue());

      // console.log('move target handle: ', handle?.id);
      // console.log('handleContainerEl: ', handle?.containerEl.id)
      // console.log('hoveredTargetId: ', this.hoveredTargetId$.getValue());
      // console.log('targetBoundingContainer: ', this.targetBoundingContainer$.getValue()?.id);
      // console.log('state: ', handle?.dropList.disabled)

      if(!(handle?.dropList.id) || handle?.dropList.id === this.hoveredTargetId$.getValue()) return;

      console.log('dropList.id: ' + handle.dropList.id + ', hoveredTargetId$: ' + this.hoveredTargetId$.getValue());

      clearTimeout(this.hoverTargetTimer!);

      const inContainer: boolean = this.pointWithinBoundingContainer(point.x, point.y);

      if(!handle) {
        // console.log('no handle, inContainer:', inContainer, 'bounds:', this.targetBoundingContainer$.getValue()?.element.nativeElement.getBoundingClientRect());
        if(!inContainer) {
          this.hoveredTargetId$.next(null);
          this.targetBoundingContainer$.next(null);

        }
        return;
      }
      //
      // console.log('container parent: ', this.targetBoundingContainer$.getValue()?.parentId);

      this.hoverTargetTimer = setTimeout(() => {
        this.hoveredTargetId$.next(handle.dropList.id);
        this.targetBoundingContainer$.next(handle.containerEl);

        //TODO Potentially clean this up with cdkDropListSortPredicate
        //would need to make it only apply to the selected list, so probably add just a sortPredicate field to the list registry
        // i dont know if this would solve the _startReceiving problem though
        if(this.targetBoundingContainer$.getValue()?.id === 'content-root') {
          handle.dropList._dropListRef.sortingDisabled = false;
          handle.dropList._dropListRef._startReceiving(event.source.dropContainer._dropListRef, event.source._dragRef as any);
          handle.dropList._dropListRef._sortItem(event.source._dragRef, point.x, point.y, event.delta);
        }

        handle.dropList._dropListRef.enter(event.source._dragRef, event.pointerPosition.x, event.pointerPosition.y);
        handle.dropList._dropListRef.exit(event.source._dragRef);

      }, 400)

    })

    this.isDragging$.pipe(
      switchMap(isDragging => isDragging ? this.mouseService.mouseUp$ : EMPTY),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(event => {
      this.resetAfterDragEnd();
    })

    this.isDragging$.pipe(
      takeUntilDestroyed(this.destroyRef),
      distinctUntilChanged(),
      filter(dragging => !dragging),
      tap(() => this.hoveredList$.next(null))
    ).subscribe();
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
    if(getDropEntityType(event.source.data) === 'member') {
      this.draggedMember$.next(event.source.data);
      this.onMemberDragMoved(event);
      return;
    }
    this.isDragging$.next(true)
    this.dragMoved$.next(event);
  }

  onMemberDragMoved(event: CdkDragMove) {
    const { x, y } = event.pointerPosition;
    const slot = document.elementFromPoint(x,y)?.closest('[data-position-id]') as HTMLElement | null;

    const positionIdString = slot?.getAttribute('data-position-id') ?? null;

    if(!positionIdString) {
      this.ngZone.run(() => {
        this.hoveredPositionId$.next(null);
      });
    }

    const newHoveredPositionId = Number(positionIdString);

    if(newHoveredPositionId !== this.hoveredPositionId$.getValue()) {
      this.ngZone.run(() => {
        this.hoveredPositionId$.next(newHoveredPositionId);
      });

      this.cdr.markForCheck();
    }
  }

  registerList(
    id: string,
    entityType: DropListEntityType,
    dropList: CdkDropList,
    element: ElementRef<HTMLElement>,
    treeDepth: number,
    parentId?: string): DropListRegistration {

    console.log("Registering " + entityType);

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
        this.allPositionLists$.next(this.droppablePositionLists);

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

        this.allSubgroupLists$.next(this.droppableSubgroupLists);

        const list = this.droppableSubgroupLists.find(sub => sub.id === id);

        if(!list) {
          throw new Error(`Subgroup list registration for id: ${id} not created/could not be found.`)
        }

        return list;
      }
    }
  }

  unregisterList(unregister: DropListRegistration) {
    console.log("Unregistering: " + unregister.entityType)
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

  unregisterHoverTarget(unregister: SubgroupHoverTargetRegistration) {
    const idx = this.hoverTargetList.indexOf(unregister);
    if(idx > -1) {
      this.hoverTargetList.splice(idx, 1);
    }
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

  public isCompatibleDrop = (dragData: CdkDrag, dropList: CdkDropList): boolean => {

    let registration = this.droppableSubgroupLists.find(
      list => list.dropList === dropList
    );

    if(!registration) {
      registration = this.droppablePositionLists.find(list => list.dropList === dropList);
    } else if('positionId' in dragData.data) {
      return false;
    }

    if(!registration) {
      return false;
    }

    const elementData = dragData.data as DropData;
    const container = this.targetBoundingContainer$.getValue()

    const droppable = Object.values(DROP_COMPATIBILITY_PREDICATES)
      .some(predicate => predicate(elementData, registration, container));

    console.log('droppable: ', droppable);

    return droppable
  }

  isCompatibleMemberDrop = (dragData: CdkDrag, dropList: CdkDropList): boolean => {
    if(getDropEntityType(dragData.data) !== 'member') return false;

    return dropList.data.length === 0;
  }

  public resetAfterDragEnd() {
    this.isDragging$.next(false);
    this.dragMoved$.next(null);
    this.hoveredTargetId$.next(null);
    this.dropDataType$.next(null);
    this.targetBoundingContainer$.next(null);
    clearTimeout(this.hoverTargetTimer!);
  }

  public clearAllRegisteredLists() {
    this.droppableSubgroupLists = [];
    this.droppablePositionLists = [];
    this.hoverTargetList = [];
    this.registeredContainers = [];

    this.allSubgroupLists$.next([]);
    this.allPositionLists$.next([]);
    this.targetBoundingContainer$.next(null);
    this.hoveredList$.next(null);
  }
}
