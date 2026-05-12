import {
  AfterViewInit,
  Component,
  ElementRef,
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
import {BehaviorSubject, debounceTime, distinctUntilChanged, Subject, takeUntil} from "rxjs";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {CdkDragHandle, CdkDragMove, CdkDropList, DragDropModule} from "@angular/cdk/drag-drop";
import {
  SubgroupManagementInteractService
} from "../../../../services/facade-services/group-management/subgroup-management-interact.service";
import {
  DropListRegistration,
  DropListRegistryService, ElementContainerRegistration
} from "../../../../services/facade-services/group-management/drop-list-registry.service";


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
  private destroy$ = new Subject<void>();

  protected thisContainerId$!: BehaviorSubject<string>;

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

  @ViewChild('chipContainerRef', {read: ElementRef }) chipContainerRef!: ElementRef<HTMLElement>;

  protected containerRegistrationRef!: ElementContainerRegistration;
  protected subgroupListRef!: DropListRegistration;
  private positionListRef!: DropListRegistration;
  protected connectedToSubgroups: CdkDropList[] = [];
  protected connectedToPositions: CdkDropList[] = [];
  protected disableNativeSubgroupsList: boolean = true;

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

  constructor(protected subgroupInteract: SubgroupManagementInteractService,
              protected dropListRegistry: DropListRegistryService){}

  ngOnInit() {
    this.selfDepth = this.parentTreeDepth++;
    if(this.collapseFromParent$ != null) {
      this.collapseFromParent$.subscribe(collapse => {
        this.selfExpanded = collapse;
        this.childrenExpanded = collapse;
        this.collapseFromSelf$.next(collapse);
      })
    }

    this.dropListRegistry.hoveredContainer$.pipe(
      takeUntil(this.destroy$),
      distinctUntilChanged((a, b) => a?.id === b?.id),
      debounceTime(100))
      .subscribe(hovered => {
        this.disableNativeSubgroupsList = hovered?.id !== this.containerRegistrationRef.id;
      }
    )

    this.dropListRegistry.allSubgroupLists$.pipe(takeUntil(this.destroy$))
      .subscribe(list => {
        this.connectedToSubgroups = list.filter(l => l.id !== this.subgroupListRef?.dropList.id);
      })

    this.dropListRegistry.allPositionLists$.pipe(takeUntil(this.destroy$))
      .subscribe(list => {
        this.connectedToPositions = list.filter(l => l.id !== this.positionListRef?.dropList.id);
      })
  }

  ngAfterViewInit() {
    this.subgroupListRef = this.dropListRegistry.registerList(`subgroup-${this.subgroup.subgroupId}`, 'subgroup',
                                          this.nativeSubgroupList, this.nativeSubgroupListElement,
                                          this.selfDepth, this.dropListParentEl?.id);
    this.positionListRef = this.dropListRegistry.registerList(`position-${this.subgroup.subgroupId}`, 'position',
                                          this.nativePositionList, this.nativePositionListElement,
                                          this.selfDepth, this.subgroupListRef.id);

    this.thisContainerId$ = new BehaviorSubject<string>(this.subgroupListRef.id);

    const containerDropLists = [this.subgroupListRef.dropList, this.positionListRef.dropList];
    this.containerRegistrationRef = this.dropListRegistry.registerContainer(this.subgroupListRef.id, 'subgroup',
                                                    containerDropLists, this.chipContainerRef, this.selfDepth,
                                                    this.parentContainer?.id)
  }

  onDragMoved(event: CdkDragMove<any>) {
    this.dropListRegistry.onDragMoved(event);
  }

  resetAfterDragReleased() {
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
    this.dropListRegistry.unregisterList(this.subgroupListRef);
    this.dropListRegistry.unregisterList(this.positionListRef);
    this.dropListRegistry.unregisterContainer(this.containerRegistrationRef);

    this.destroy$.next();
    this.destroy$.complete();
  }
}
