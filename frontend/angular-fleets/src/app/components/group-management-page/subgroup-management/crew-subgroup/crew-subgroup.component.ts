import {
  AfterViewInit, ChangeDetectorRef,
  Component,
  ElementRef, inject,
  Input,
  OnDestroy,
  OnInit,
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
  Observable,
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



@Component({
  selector: 'app-crew-subgroup',
  imports: [
    CrewPositionChipComponent,
    NgForOf,
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
export class CrewSubgroupComponent implements OnInit, AfterViewInit, OnDestroy {
  protected dropListRegistry = inject(DropListRegistryService);
  protected groupManagementUiPrefs = inject(GroupManagementUiPrefsService);

  private destroy$ = new Subject<void>();

  @Input() subgroup!: GroupCompSubgroupViewModel;
  @Input() collapseFromParent$!: BehaviorSubject<boolean>;
  @Input() collapseAllFromRoot$!: BehaviorSubject<boolean>;
  @Input() collapseChildrenFromRoot$!: BehaviorSubject<boolean>;


  @Input() dropListParentEl!: DropListRegistration;
  @Input() parentTreeDepth!: number;
  @Input() parentContainer!: ElementContainerRegistration;
  protected selfDepth!: number;
  protected selfDropListOrientation!: DropListOrientation;

  @ViewChild('nativeSubgroupList') nativeSubgroupList!: CdkDropList;
  @ViewChild('nativeSubgroupListElement', {read: ElementRef }) nativeSubgroupListElement!: ElementRef<HTMLElement>;

  @ViewChild('nativePositionList') nativePositionList!: CdkDropList;
  @ViewChild('nativePositionListElement', {read: ElementRef }) nativePositionListElement!: ElementRef<HTMLElement>;

  @ViewChild('subgroupHoverTarget', {read: ElementRef}) subgroupHoverTarget!: ElementRef<HTMLElement>;

  @ViewChild('chipWrapperContainer', {read: ElementRef }) chipWrapperContainer!: ElementRef<HTMLElement>;
  protected thisDropListId$= new BehaviorSubject<string | null>(null);

  protected isHoveredTarget$: Observable<boolean> = new Observable<boolean>;

  protected containerRegistrationRef!: ElementContainerRegistration;
  protected subgroupListRegistrationRef!: DropListRegistration;
  private positionListRegistrationRef!: DropListRegistration;
  private subgroupHoverTargetRegistrationRef!: SubgroupHoverTargetRegistration;
  protected connectedToSubgroups: CdkDropList[] = [];
  protected connectedToPositions: CdkDropList[] = [];

  // collapseFromSelf$ is the collapse state passed as input to children
  protected selfCollapsedStatePropagatedToChildren$ = new BehaviorSubject<boolean>(true);

  // selfExpanded is used to enable/disable expansion styles for this individual instance of this component
  protected selfExpanded: boolean = this.collapseAllFromRoot$ !== null ? false : this.collapseFromParent$.getValue();

  // childrenExpanded tracks the expansion state of the nested children for each instance of this component
  // to connect the states of the different toggle button functionalities (collapse self vs. collapse children)
  protected childrenExpanded: boolean = this.collapseChildrenFromRoot$ !== null ? true : this.collapseFromParent$.getValue();

  protected get listNativeAssignedPositionsCount(): number {
    return this.subgroup.crewPositions.filter(p => p.assignedMember !== null).length;
  }

  protected get listNativeTotalPositionsCount(): number {
    return this.subgroup.crewPositions.length;
  }

  //todo this needs to be recursive and then the above version needs to just be native level
  protected get assignedPositionsCount(): number {
    return this.subgroup.crewPositions.filter(p => p.assignedMember != null).length;
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

  constructor(protected subgroupInteract: SubgroupManagementInteractService,
              private cdr: ChangeDetectorRef){}

  ngOnInit() {
    this.selfDepth = this.parentTreeDepth + 1;

    this.setDropListOrientationFromPrefs();

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
        this.collapseFromParent$.next(this.selfCollapsedStatePropagatedToChildren$.getValue())
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

    this.cdr.detectChanges()
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

    this.dropListRegistry.pageDataLoading = false;

    this.isHoveredTarget$ = this.dropListRegistry.hoveredTargetId$.pipe(
      map(hoveredId => hoveredId === this.thisDropListId$.getValue()),
    );

    console.log(`Subgroup ${this.subgroupListRegistrationRef.id} orientation: ${this.selfDropListOrientation} \n depth: ${this.selfDepth}`);
  }

  onEntered(e: CdkDragEnter) { console.log('ENTERED:', e.container.id)};
  onExited(e: CdkDragExit) { console.log('EXITED', e.container.id);}

  dragStarted(event: CdkDragStart) {
    // console.log('list sortingDisabled: ', event.source.dropContainer.sortingDisabled);
    console.log('entityType: ', getDropEntityType(event.source.data));
  }

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

  toggleCollapseSelf() {
    this.selfExpanded = !this.selfExpanded;

    this.childrenExpanded = this.selfExpanded;

    this.selfCollapsedStatePropagatedToChildren$.next(this.selfExpanded);
  }

  collapseChildren() {
    if(!this.selfExpanded) {
      this.selfExpanded = true;
    }

    this.childrenExpanded = !this.childrenExpanded;
    this.selfCollapsedStatePropagatedToChildren$.next(this.childrenExpanded);
  }

  setDropListOrientationFromPrefs() {
    const rootOrientation = this.groupManagementUiPrefs.groupManagementUiPrefs.groupCompositionPrefs.rootDropListOrientation;

    if(rootOrientation === 'horizontal') {
      this.selfDropListOrientation = 'vertical';
    } else {
      if(this.selfDepth === 0) {
        this.selfDropListOrientation = 'horizontal'
      } else {
        this.selfDropListOrientation = 'vertical';
      }
    }
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
}
