import {
  AfterViewInit, ChangeDetectorRef,
  Component,
  ElementRef, EventEmitter, inject,
  Input,
  OnDestroy,
  OnInit, Output,
  ViewChild,
} from '@angular/core';
import {
  GroupCompSubgroupViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";
import {CrewPositionChipComponent} from "../crew-position-chip/crew-position-chip.component";
import {AsyncPipe, JsonPipe, NgForOf, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {
  BehaviorSubject,
  combineLatest,
  debounceTime,
  distinctUntilChanged,
  filter,
  Observable,
  Subject,
  takeUntil, withLatestFrom
} from "rxjs";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {
  CdkDrag,
  CdkDragEnter,
  CdkDragExit,
  CdkDragHandle,
  CdkDragMove, CdkDragRelease, CdkDragStart,
  CdkDropList,
  DragDropModule
} from "@angular/cdk/drag-drop";
import {
  GroupCompPositionsBrief,
  SubgroupManagementInteractService
} from "../../../../services/facade-services/group-management/subgroup-management-interact.service";
import {
  DROP_COMPATIBILITY_PREDICATES,
  DropData,
  DropListRegistration,
  DropListRegistryService, ElementContainerRegistration, SubgroupHoverTargetRegistration
} from "../../../../services/facade-services/group-management/drop-list-registry.service";
import {environment} from "../../../../../environments/environment";
import {map, tap} from "rxjs/operators";
import {MatIconButton} from "@angular/material/button";


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

  private destroy$ = new Subject<void>();

  @Input() subgroup!: GroupCompSubgroupViewModel;
  @Input() collapseFromParent$!: BehaviorSubject<boolean>;

  @Input() dropListParentEl!: DropListRegistration;
  @Input() parentTreeDepth!: number;
  @Input() parentContainer!: ElementContainerRegistration;
  protected selfDepth!: number;

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

  protected collapseFromSelf$ = new BehaviorSubject<boolean>(true);
  protected selfExpanded: boolean = true;
  protected childrenExpanded: boolean = true;

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
    this.selfDepth = this.parentTreeDepth++;
    if(this.collapseFromParent$ != null) {
      this.collapseFromParent$.subscribe(collapse => {
        this.selfExpanded = collapse;
        this.childrenExpanded = collapse;
        this.collapseFromSelf$.next(collapse);
      })
    }

    this.dropListRegistry.allSubgroupLists$.pipe(takeUntil(this.destroy$))
      .subscribe(list => {
        queueMicrotask(() => {
          this.connectedToSubgroups = list.filter(l => l.id !== this.nativeSubgroupList?.id);
        });
      });

    this.dropListRegistry.allPositionLists$.pipe(takeUntil(this.destroy$))
      .subscribe(list => {
        queueMicrotask(() => {
          this.connectedToPositions = list.filter(l => l.id !== this.nativePositionList?.id);
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
  }

  onEntered(e: CdkDragEnter) { console.log('ENTERED:', e.container.id)};
  onExited(e: CdkDragExit) { console.log('EXITED', e.container.id);}

  dragStarted(event: CdkDragStart) {
    console.log('list sortingDisabled: ', event.source.dropContainer.sortingDisabled);

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

    this.collapseFromSelf$.next(this.selfExpanded);
  }

  collapseChildren() {
    if(!this.selfExpanded) {
      this.selfExpanded = true;
    }

    this.childrenExpanded = !this.childrenExpanded;
    this.collapseFromSelf$.next(this.childrenExpanded);
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
