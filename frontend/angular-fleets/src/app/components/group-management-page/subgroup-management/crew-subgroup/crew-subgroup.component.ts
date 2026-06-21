import {
  AfterViewInit,
  Component,
  ElementRef, EventEmitter, inject,
  Input, OnChanges,
  OnDestroy,
  OnInit, Output, SimpleChanges,
  ViewChild,
} from '@angular/core';
import {
  GroupCompSubgroupViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";
import {CrewPositionChipComponent} from "../crew-position-chip/crew-position-chip.component";
import {AsyncPipe, NgIf} from "@angular/common";
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
  CdkDragHandle,
  CdkDragMove, CdkDragRelease, CdkDragStart,
  CdkDropList,
  DragDropModule, DropListOrientation
} from "@angular/cdk/drag-drop";
import {
  GroupCompositionInteractService
} from "../../../../services/facade-services/group-management/group-composition-interact.service";
import {
  DropListRegistration,
  DropListRegistryService, ElementContainerRegistration, getDropEntityType, SubgroupHoverTargetRegistration
} from "../../../../services/facade-services/group-management/drop-list-registry.service";
import {map} from "rxjs/operators";
import {
  GroupManagementUiPrefsService
} from "../../../../services/facade-services/group-management/group-management-ui-prefs/group-management-ui-prefs.service";
import {toTitleCase} from "../../../../utils/global-functions";
import {
  EditSubgroupLabelInputComponent
} from "../edit-subgroup-label-input/edit-subgroup-label-input.component";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmGenericComponent} from "../../../pop-ups/confirm-generic/confirm-generic.component";
import {
  GroupManagementInteractService
} from "../../../../services/facade-services/group-management/group-management-interact.service";
import {
  GroupCompCrewPositionViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-crew-position-view-model";

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
    EditSubgroupLabelInputComponent,
  ],
  templateUrl: './crew-subgroup.component.html',
  styleUrl: './crew-subgroup.component.css'
})
export class CrewSubgroupComponent implements OnInit, AfterViewInit, OnChanges, OnDestroy {
  protected dropListRegistry = inject(DropListRegistryService);
  protected groupManagementUiPrefs = inject(GroupManagementUiPrefsService);

  private destroy$ = new Subject<void>();

  @Input({ required: true}) subgroup!: GroupCompSubgroupViewModel;
  @Input() expandedFromParent$?: BehaviorSubject<boolean>;
  @Input() rootChildrenExpanded$?: BehaviorSubject<boolean>;

  @Input() dropListParentEl!: DropListRegistration;
  @Input() parentTreeDepth!: number;
  @Input() parentContainer!: ElementContainerRegistration;
  protected selfDepth!: number;

  @Output() emitDeleteSubgroup = new EventEmitter<GroupCompSubgroupViewModel>;
  @Output() emitEditingLabel = new EventEmitter<boolean>;

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
  protected selfExpandedPropagateToChildren$ = new BehaviorSubject<boolean>(false);

  // selfExpanded is used to enable/disable expansion styles for this individual instance of this component
  protected selfExpanded: boolean = true;

  // childrenExpanded tracks the expansion state of the nested children for each instance of this component
  // to connect the states of the different toggle button functionalities (collapse self vs. collapse children)
  protected childrenExpanded: boolean = true;

  protected positionsExpanded: boolean = true;

  protected nativeTogglesNextOrientation!: DropListOrientation;

  // 'editingTitle' is used in the template for the subgroup where the subgroup label is being edited.
  protected editingTitle: boolean = false;
  // 'childEditingTitle' is used in that subgroups parent to disable cdkDrag while editing (so text can be highlighted etc.)
  protected childEditingLabel: boolean = false;

  //horizontal orientation only
  protected canScrollHorizontal$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  protected showLeftScroll: boolean = false;
  protected showRightScroll: boolean = false;

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

  constructor(protected compositionInteract: GroupCompositionInteractService,
              private managementInteract: GroupManagementInteractService,
              private dialog: MatDialog){}

  ngOnInit() {
    this.nativeSubgroupsForDisplay = this.subgroup.subgroups;
    this.selfDepth = this.parentTreeDepth + 1;

    this.selfExpanded = this.expandedFromParent$?.getValue() ?? true;
    this.childrenExpanded = this.rootChildrenExpanded$?.getValue() ?? true;
    this.positionsExpanded = this.rootChildrenExpanded$?.getValue() ?? true;
    this.selfExpandedPropagateToChildren$.next(this.selfExpanded);

    if(this.subgroup.dropListOrientation === 'horizontal') {
      this.nativeTogglesNextOrientation = 'vertical';
    } else if(this.subgroup.dropListOrientation === 'vertical') {
      this.nativeTogglesNextOrientation = 'mixed';
    } else {
      this.nativeTogglesNextOrientation = 'horizontal';
    }

    if(this.expandedFromParent$ != null) {
      this.expandedFromParent$.pipe(takeUntil(this.destroy$))
        .subscribe(collapse => {
        this.selfExpanded = collapse;
        this.childrenExpanded = collapse;
        this.positionsExpanded = collapse;
        this.selfExpandedPropagateToChildren$.next(collapse);
      })
    }

    if(this.rootChildrenExpanded$ != null) {
      this.rootChildrenExpanded$.pipe(
        takeUntil(this.destroy$),
        distinctUntilChanged())
        .subscribe(expanded => {
          this.childrenExpanded = expanded;
          this.positionsExpanded = expanded;
          this.selfExpandedPropagateToChildren$.next(expanded);
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

    this.updateHorizontalScrollButtonVisibility();

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

  // dragStarted(event: CdkDragStart) {
  //   console.log('list sortingDisabled: ', event.source.dropContainer.sortingDisabled);
  //   console.log('entityType: ', getDropEntityType(event.source.data));
  // }

  move_disableSorting() {
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
    this.compositionInteract.reorientingDropList = true;

    const actionLabel = 'Change List Orientation';
    this.compositionInteract.pushSubgroupActionToHistoryCache(actionLabel);

    const current = this.subgroup.dropListOrientation;

    if(current === 'horizontal') {
      this.subgroup.dropListOrientation = 'vertical';
      this.nativeTogglesNextOrientation = 'mixed';
      this.canScrollHorizontal$.next(false);
    } else if(current === 'vertical') {
      this.subgroup.dropListOrientation = 'mixed';
      this.nativeTogglesNextOrientation = 'horizontal';
      this.canScrollHorizontal$.next(false);
    } else {
      this.subgroup.dropListOrientation = 'horizontal'
      this.nativeTogglesNextOrientation = 'vertical'
      setTimeout(() => this.updateHorizontalScrollButtonVisibility(), 500);
    }

    this.compositionInteract.updateSubgroupDropListOrientation(this.subgroup);

    setTimeout(() => this.compositionInteract.reorientingDropList = false, 500);
  }

  toggleCollapseSelf() {
    this.selfExpanded = !this.selfExpanded;

    this.childrenExpanded = this.selfExpanded;
    this.positionsExpanded = this.selfExpanded;

    this.selfExpandedPropagateToChildren$.next(this.selfExpanded);
  }

  toggleCollapsePositions() {
    this.positionsExpanded = !this.positionsExpanded;
  }

  toggleCollapseChildren() {
    if(!this.selfExpanded) {
      this.selfExpanded = true;
    }

    this.childrenExpanded = !this.childrenExpanded;
    this.positionsExpanded = this.childrenExpanded;
    this.selfExpandedPropagateToChildren$.next(this.childrenExpanded);
  }

  scrollHorizontal(dir: 'left' | 'right') {
    const scrollSegment = this.nativeSubgroupListElement.nativeElement.clientWidth * 0.5;

    this.nativeSubgroupListElement.nativeElement.scrollBy({left: dir === 'right' ? scrollSegment : -scrollSegment, behavior: 'smooth'});
  }

  get chipSelfOrientationVertical(): boolean {
    const rootIsVertical = this.groupManagementUiPrefs.getRootDropListOrientation === 'vertical';
    const selfDepthIsZero = false;

    return false;
  }

  updateHorizontalScrollButtonVisibility() {
    const el = this.nativeSubgroupListElement.nativeElement;
    const max = el.scrollWidth - el.clientWidth;
    this.canScrollHorizontal$.next((max > 1) && (this.subgroup.dropListOrientation === 'horizontal'));
    this.showLeftScroll = el.scrollLeft > 5;
    this.showRightScroll = el.scrollLeft < max - 1;
  }

  catchChildDeleteSubgroupEmission(forDelete: GroupCompSubgroupViewModel) {
    const dialogRef = this.dialog.open(ConfirmGenericComponent, {
      data: {
        message: 'Delete this subgroup and all of its structurally nested contents? Any group members assigned ' +
          'to this group will have their position assignment reset.',
        title: 'Subgroup: \"' + forDelete.subgroupLabel + '\", and its contents.',
      }
    });

    dialogRef.afterClosed().pipe(takeUntil(this.destroy$))
      .subscribe(result => {
        if (result) {

          const actionLabel = 'Delete Subgroup';
          this.compositionInteract.pushSubgroupActionToHistoryCache(actionLabel);

          this.subgroup.subgroups = this.subgroup.subgroups.filter(
            sub => sub.subgroupId !== forDelete.subgroupId);

          this.nativeSubgroupsForDisplay = this.subgroup.subgroups;

          this.compositionInteract.persistState().pipe(takeUntil(this.destroy$))
            .subscribe({
                next: () => {
                  this.managementInteract.fetchActiveRoster(this.managementInteract.groupId);
                }
          });
        }
      });
  }

  catchPositionDeleteEmission(posForDelete: GroupCompCrewPositionViewModel) {
    const actionLabel = 'Delete Position';

    this.compositionInteract.pushSubgroupActionToHistoryCache(actionLabel);

    this.subgroup.crewPositions = this.subgroup.crewPositions.filter(
      p => p.positionId !== posForDelete.positionId);

    this.compositionInteract.deleteCrewPosition(posForDelete);
  }

  enableSubgroupLabelEditing() {
    this.editingTitle = true;
    this.emitEditingLabel.emit(true);
  }

  updateChildEditingLabel(isEditing: boolean) {
    this.childEditingLabel = isEditing;
  }

  updateSubgroupLabel(newLabel: string) {
    const actionLabel = 'Update Subgroup Label'
    this.compositionInteract.pushSubgroupActionToHistoryCache(actionLabel);

    this.compositionInteract.updateSubgroupLabel(this.subgroup.subgroupId, newLabel);
    this.subgroup.subgroupLabel = newLabel;
    this.editingTitle = false;
    this.emitEditingLabel.emit(false);
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
