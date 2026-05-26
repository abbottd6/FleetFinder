import {
  AfterViewChecked,
  AfterViewInit,
  Component,
  ElementRef, inject,
  Input, OnChanges,
  OnDestroy,
  OnInit, SimpleChanges,
  ViewChild,
} from '@angular/core';
import {
  GroupCompSubgroupViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";
import {CrewPositionChipComponent} from "../crew-position-chip/crew-position-chip.component";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {
  BehaviorSubject,
  combineLatest, distinctUntilChanged,
  Observable, of,
  Subject,
  takeUntil,
} from "rxjs";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {
  CdkDragEnter,
  CdkDragExit,
  CdkDragHandle,
  CdkDragMove, CdkDragRelease, CdkDragStart,
  CdkDropList,
  DragDropModule, DropListOrientation
} from "@angular/cdk/drag-drop";
import {
  SubgroupManagementInteractService
} from "../../../../services/facade-services/group-management/subgroup-management-interact.service";
import {
  DropListRegistration,
  DropListRegistryService, ElementContainerRegistration, getDropEntityType, SubgroupHoverTargetRegistration
} from "../../../../services/facade-services/group-management/drop-list-registry.service";
import {map} from "rxjs/operators";
import {
  GroupManagementUiPrefsService
} from "../../../../services/facade-services/group-management/group-management-ui-prefs/group-management-ui-prefs.service";
import {toTitleCase} from "../../../../utils/global-functions";

@Component({
  selector: 'app-crew-subgroup',
  imports: [
    CrewPositionChipComponent,
    NgIf,
    MatIcon,
    MatTooltip,
    MatMenuTrigger,
    MatMenu,
    MatMenuItem,
    DragDropModule,
    CdkDragHandle,
    AsyncPipe,
  ],
  templateUrl: './crew-subgroup.component.html',
  styleUrl: './crew-subgroup.component.css'
})
export class CrewSubgroupComponent implements OnInit, AfterViewInit, OnChanges, OnDestroy {
  protected dropListRegistry = inject(DropListRegistryService);
  protected groupManagementUiPrefs = inject(GroupManagementUiPrefsService);

  private destroy$ = new Subject<void>();

  @Input({ required: true}) subgroup!: GroupCompSubgroupViewModel;
  @Input() collapseFromParent$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  @Input() collapseAllFromRoot$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  @Input() collapseChildrenFromRoot$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  @Input() dropListParentEl!: DropListRegistration;
  @Input() parentTreeDepth!: number;
  @Input() parentContainer!: ElementContainerRegistration;
  protected selfDepth!: number;

  protected nativeSubgroupsForDisplay: GroupCompSubgroupViewModel[] = [];

  @ViewChild('nativeSubgroupList') nativeSubgroupList!: CdkDropList;
  @ViewChild('nativeSubgroupListElement', {read: ElementRef }) nativeSubgroupListElement!: ElementRef<HTMLElement>;

  @ViewChild('nativePositionList') nativePositionList!: CdkDropList;
  @ViewChild('nativePositionListElement', {read: ElementRef }) nativePositionListElement!: ElementRef<HTMLElement>;

  @ViewChild('subgroupHoverTarget', {read: ElementRef}) subgroupHoverTarget!: ElementRef<HTMLElement>;

  @ViewChild('chipWrapperContainer', {read: ElementRef }) chipWrapperContainer!: ElementRef<HTMLElement>;
  protected thisDropListId$= new BehaviorSubject<string | null>(null);

  protected isHoveredTarget$: Observable<boolean> = of(false);

  protected containerRegistrationRef!: ElementContainerRegistration;
  protected subgroupListRegistrationRef!: DropListRegistration;
  private positionListRegistrationRef!: DropListRegistration;
  private subgroupHoverTargetRegistrationRef!: SubgroupHoverTargetRegistration;
  protected connectedToSubgroups: CdkDropList[] = [];
  protected connectedToPositions: CdkDropList[] = [];

  // collapseFromSelf$ is the collapse state passed as input to children
  protected selfCollapsedStatePropagatedToChildren$ = new BehaviorSubject<boolean>(true);

  // selfExpanded is used to enable/disable expansion styles for this individual instance of this component
  protected selfExpanded!: boolean;

  // childrenExpanded tracks the expansion state of the nested children for each instance of this component
  // to connect the states of the different toggle button functionalities (collapse self vs. collapse children)
  protected childrenExpanded!: boolean;

  protected positionsExpanded!: boolean;

  protected nativeTogglesNextOrientation!: DropListOrientation;

  protected get listNativeAssignedPositionsCount(): number {
    return this.subgroup.crewPositions.filter(p => p.assignedMember !== null).length;
  }

  protected get listNativeTotalPositionsCount(): number {
    return this.subgroup.crewPositions.length;
  }

  protected get assignedPositionsCountTotalNested(): number {
    return this.recursivelyCountAssignedNestedPositionsInTree(this.subgroup);
  }

  protected get totalPositionsCount(): number {
    return this.subgroup.crewPositions.length + this.subgroup.subgroups.reduce((sum, subgroup) =>
      sum + this.recursivelyCountPositionsInTree(subgroup), 0);
  }

  protected get totalSubgroupsCount(): number {
    return this.subgroup.subgroups.length + this.subgroup.subgroups.reduce((sum, subgroup) =>
      sum + this.recursivelyCountSubgroupChildren(subgroup), 0);
  }

  private recursivelyCountPositionsInTree(subgroup: GroupCompSubgroupViewModel): number {
    return subgroup.crewPositions.length +
      subgroup.subgroups.reduce((sum, child) =>
        sum + this.recursivelyCountPositionsInTree(child), 0);
  }

  private recursivelyCountAssignedNestedPositionsInTree(subgroup: GroupCompSubgroupViewModel): number {
    return subgroup.crewPositions.filter(pos => pos.assignedMember !== null).length +
      subgroup.subgroups.reduce((sum, child) =>
        sum + this.recursivelyCountAssignedNestedPositionsInTree(child), 0);
  }

  private recursivelyCountSubgroupChildren(subgroup: GroupCompSubgroupViewModel): number {
    return subgroup.subgroups.length +
      subgroup.subgroups.reduce((sum, child) =>
        sum + this.recursivelyCountSubgroupChildren(child), 0);
  }

  protected get hasChildSubgroups(): boolean {
    return this.subgroup.subgroups.length > 0;
  }

  protected displayListEntryBlocker$: Observable<boolean> =  combineLatest([
    this.dropListRegistry.isDragging$,
    this.dropListRegistry.dropDataType$,
    this.isHoveredTarget$
  ]).pipe(
    map(([dragging, dataType, isHovered]) =>
      dragging && (dataType === 'subgroup') && !isHovered)
  )

  constructor(protected subgroupInteract: SubgroupManagementInteractService){}

  ngOnInit() {
    this.nativeSubgroupsForDisplay = this.subgroup.subgroups;
    this.selfDepth = this.parentTreeDepth + 1;

    this.selfExpanded = this.collapseAllFromRoot$.getValue() ? this.collapseFromParent$.getValue() : false;
    this.childrenExpanded = this.collapseChildrenFromRoot$.getValue() ? this.collapseFromParent$.getValue() : false;
    this.positionsExpanded = this.childrenExpanded;

    if(this.subgroup.dropListOrientation === 'horizontal') {
      this.nativeTogglesNextOrientation = 'vertical';
    } else if(this.subgroup.dropListOrientation === 'vertical') {
      this.nativeTogglesNextOrientation = 'mixed';
    } else {
      this.nativeTogglesNextOrientation = 'horizontal';
    }

    if(this.collapseFromParent$ != null) {
      this.collapseFromParent$.pipe(takeUntil(this.destroy$))
        .subscribe(collapse => {
        this.selfExpanded = collapse;
        this.childrenExpanded = collapse;
        this.selfCollapsedStatePropagatedToChildren$.next(collapse);
      })
    }

    if(this.collapseAllFromRoot$ != null) {

      if(this.collapseAllFromRoot$.getValue()) {
        this.selfExpanded = false;
        this.childrenExpanded = false;
        this.collapseFromParent$?.next(this.selfCollapsedStatePropagatedToChildren$.getValue())
        this.selfCollapsedStatePropagatedToChildren$.next(false);
      }

      this.collapseAllFromRoot$.pipe(
        takeUntil(this.destroy$),
        distinctUntilChanged()
      ).subscribe(collapse => {
        this.selfExpanded = !this.selfExpanded;
        this.selfCollapsedStatePropagatedToChildren$.next(false);
      })
    }

    if(this.collapseChildrenFromRoot$ != null) {
      this.collapseChildrenFromRoot$.pipe(
        takeUntil(this.destroy$),
        distinctUntilChanged())
        .subscribe(collapse => {
          this.childrenExpanded = !this.childrenExpanded;
          this.selfCollapsedStatePropagatedToChildren$.next(this.childrenExpanded);
      })
    }

    this.dropListRegistry.allSubgroupLists$.pipe(takeUntil(this.destroy$))
      .subscribe(registeredLists => {
        queueMicrotask(() => {
          this.connectedToSubgroups = registeredLists.filter(l => l.dropList.id !== this.nativeSubgroupList?.id)
            .map(listReg => listReg.dropList)
        });
      });

    this.dropListRegistry.allPositionLists$.pipe(takeUntil(this.destroy$))
      .subscribe(registeredLists => {
        queueMicrotask(() => {
          this.connectedToPositions = registeredLists.filter(l => l.id !== this.nativePositionList?.id)
            .map(listReg => listReg.dropList)
        });
      });
  }

  ngAfterViewInit() {
    this.nativeSubgroupList.sortingDisabled = false;

    this.subgroupListRegistrationRef = this.dropListRegistry.registerList(`subgroup-${this.subgroup.subgroupId}`,
      'subgroup', this.nativeSubgroupList, this.nativeSubgroupListElement,
      this.selfDepth, this.dropListParentEl?.id ?? 'content-root');

    this.positionListRegistrationRef = this.dropListRegistry.registerList(`position-${this.subgroup.subgroupId}`,
      'position', this.nativePositionList, this.nativePositionListElement,
      this.selfDepth, this.subgroupListRegistrationRef.id);

    this.thisDropListId$.next(this.nativeSubgroupList.id);

    const containerDropLists = [this.subgroupListRegistrationRef.dropList, this.positionListRegistrationRef.dropList];
    this.containerRegistrationRef = this.dropListRegistry.registerContainer(this.subgroupListRegistrationRef.id, 'subgroup',
      containerDropLists, this.chipWrapperContainer, this.selfDepth, this.parentContainer?.id ?? 'content-root');

    this.subgroupHoverTargetRegistrationRef = this.dropListRegistry.registerHoverTarget(this.nativeSubgroupList.id,
      this.nativeSubgroupList, this.subgroupHoverTarget, this.containerRegistrationRef, this.selfDepth);

    this.isHoveredTarget$ = this.dropListRegistry.hoveredTargetId$.pipe(
      map(hoveredId => hoveredId === this.thisDropListId$.getValue()),
    );
  }

  ngOnChanges(changes: SimpleChanges) {
    if(changes['subgroup']) {
      this.nativeSubgroupsForDisplay = [...(this.subgroup.subgroups ?? [])];
    }
  }

  dragStarted(event: CdkDragStart) {
    // console.log('list sortingDisabled: ', event.source.dropContainer.sortingDisabled);
    console.log('entityType: ', getDropEntityType(event.source.data));
  }

  move_disableSorting() {
    console.log('parent: ', this.parentContainer.dropLists[0].id);
    this.parentContainer.dropLists[0].sortingDisabled = true;
  }

  onDragMoved(event: CdkDragMove<any>) {
    this.dropListRegistry.onDragMoved(event);
  }

  resetAfterDragReleased(event: CdkDragRelease) {
    event.source.dropContainer.sortingDisabled = false;

    setTimeout(() => this.dropListRegistry.resetAfterDragEnd(), 300);
  }

  toggleDropListOrientation() {
    this.subgroupInteract.reorientingDropList = true;
    const current = this.subgroup.dropListOrientation;

    if(current === 'horizontal') {
      this.subgroup.dropListOrientation = 'vertical';
      this.nativeTogglesNextOrientation = 'mixed';
    } else if(current === 'vertical') {
      this.subgroup.dropListOrientation = 'mixed';
      this.nativeTogglesNextOrientation = 'horizontal';
    } else {
      this.subgroup.dropListOrientation = 'horizontal'
      this.nativeTogglesNextOrientation = 'vertical'
    }

    this.subgroupInteract.updateSubgroupDropListOrientation(this.subgroup);

    setTimeout(() => this.subgroupInteract.reorientingDropList = false, 500);
  }

  toggleCollapseSelf() {
    this.selfExpanded = !this.selfExpanded;

    this.childrenExpanded = this.selfExpanded;
    this.positionsExpanded = this.childrenExpanded;

    this.selfCollapsedStatePropagatedToChildren$.next(this.selfExpanded);
  }

  toggleCollapsePositions() {
    this.positionsExpanded = !this.positionsExpanded;
  }

  collapseChildren() {
    if(!this.selfExpanded) {
      this.selfExpanded = true;
    }

    this.childrenExpanded = !this.childrenExpanded;
    this.positionsExpanded = this.childrenExpanded;
    this.selfCollapsedStatePropagatedToChildren$.next(this.childrenExpanded);
  }

  get chipSelfOrientationVertical(): boolean {
    const rootIsVertical = this.groupManagementUiPrefs.getRootDropListOrientation === 'vertical';
    const selfDepthIsZero = false;

    return false;
  }

  ngOnDestroy() {
    this.dropListRegistry.unregisterHoverTarget(this.subgroupHoverTargetRegistrationRef);
    this.dropListRegistry.unregisterList(this.subgroupListRegistrationRef);
    this.dropListRegistry.unregisterList(this.positionListRegistrationRef);
    this.dropListRegistry.unregisterContainer(this.containerRegistrationRef);

    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly toTitleCase = toTitleCase;
}
