import {
  afterNextRender,
  AfterViewInit,
  Component,
  ElementRef,
  inject,
  Input,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';
import {
  BehaviorSubject,
  combineLatest,
  filter,
  Subject,
  take,
  takeUntil
} from "rxjs";
import {
  DropListRegistration, DropListRegistryService,
  ElementContainerRegistration, SubgroupHoverTargetRegistration
} from "../../../../services/facade-services/group-management/drop-list-registry.service";
import {
  CdkDrag,
  CdkDragHandle,
  CdkDragRelease,
  CdkDropList,
  CdkDropListGroup,
  DropListOrientation
} from "@angular/cdk/drag-drop";
import {
  SubgroupManagementInteractService
} from "../../../../services/facade-services/group-management/subgroup-management-interact.service";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {CrewSubgroupComponent} from "../crew-subgroup/crew-subgroup.component";
import {MatIcon} from "@angular/material/icon";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatTooltip} from "@angular/material/tooltip";
import {map} from "rxjs/operators";
import {
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";
import {
  GroupManagementUiPrefsService
} from "../../../../services/facade-services/group-management/group-management-ui-prefs/group-management-ui-prefs.service";
import {MatProgressSpinner} from "@angular/material/progress-spinner";

@Component({
  selector: 'app-root-subgroup',
  imports: [
    AsyncPipe,
    CdkDrag,
    CdkDragHandle,
    CdkDropList,
    CrewSubgroupComponent,
    MatIcon,
    MatMenu,
    MatMenuItem,
    MatTooltip,
    NgForOf,
    NgIf,
    MatMenuTrigger,
    CdkDropListGroup,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatProgressSpinner
  ],
  templateUrl: './root-subgroup.component.html',
  styleUrl: './root-subgroup.component.css'
})
export class RootSubgroupComponent implements OnInit, AfterViewInit, OnDestroy {
  private destroy$ = new Subject<void>();

  protected readonly dropListRegistry = inject(DropListRegistryService);
  protected readonly groupManagementUiPrefs = inject(GroupManagementUiPrefsService);

  protected reorientingDropList: boolean = false;

  @Input() listingTitle!: string;
  @Input() groupId!: number;

  @ViewChild('rootSubgroupList') rootSubgroupList!: CdkDropList;
  @ViewChild('rootSubgroupListElement', {read: ElementRef }) rootSubgroupListElement!: ElementRef<HTMLElement>;
  @ViewChild('rootListHoverTarget', {read: ElementRef}) rootListHoverTarget!: ElementRef<HTMLElement>;
  @ViewChild('groupCompRootContainer', {read: ElementRef }) groupCompRootContainer!: ElementRef<HTMLElement>;

  protected rootContainerRegistrationRef!: ElementContainerRegistration;
  protected rootListRegistrationRef!: DropListRegistration;
  protected rootHoverTargetRegistrationRef!: SubgroupHoverTargetRegistration;
  protected connectedToSubgroups: CdkDropList[] = [];

  protected rootDropListId$ = new BehaviorSubject<string>('');

  protected rootOrientation: DropListOrientation = this.groupManagementUiPrefs.getRootDropListOrientation;

  protected isHoveredTarget$ = combineLatest([
    this.dropListRegistry.hoveredTargetId$,
    this.rootDropListId$
  ]).pipe(
    map(([hoveredId, rootId]) => hoveredId === rootId)
  )

  protected collapseAll$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  protected collapseRootChildrenNotRoots$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);

  // protected displayListEntryBlocker$: Observable<boolean> =  combineLatest([
  //   this.dropListRegistry.isDragging$,
  //   this.dropListRegistry.dropDataType$,
  //   this.isHoveredTarget$
  // ]).pipe(
  //   map(([dragging, dataType, isHovered]) =>
  //     dragging && (dataType === 'subgroup') && !isHovered),
  // )

  constructor(protected subgroupInteract: SubgroupManagementInteractService){
    afterNextRender(() => {
      this.subgroupInteract.subgroupTrees$.pipe(
        filter(trees => trees?.length > 0),
        take(1),
        takeUntil(this.destroy$)
      ).subscribe(() => {
        this.rootListRegistrationRef = this.dropListRegistry.registerList('content-root', 'root',
          this.rootSubgroupList, this.rootSubgroupListElement, 0, 'root');

        this.rootDropListId$.next(this.rootSubgroupList.id);

        this.rootContainerRegistrationRef = this.dropListRegistry.registerContainer(this.rootListRegistrationRef.id, 'root',
          [this.rootSubgroupList], this.groupCompRootContainer, 0, 'content-root');

        this.rootHoverTargetRegistrationRef = this.dropListRegistry.registerHoverTarget(this.rootSubgroupList.id,
          this.rootSubgroupList, this.rootListHoverTarget, this.rootContainerRegistrationRef, 0);

        this.dropListRegistry.allSubgroupLists$.pipe(takeUntil(this.destroy$))
          .subscribe(registeredLists => {
            this.connectedToSubgroups = registeredLists.filter(l => l.id !== this.rootSubgroupList?.id)
              .map(regList => regList.dropList);
          })

        console.log(this.rootContainerRegistrationRef.element.nativeElement.getBoundingClientRect());
      })
    })
  }

  ngOnInit() {
    // this.isHoveredTarget$.pipe(
    //   takeUntil(this.destroy$),
    //   distinctUntilChanged(),
    //   tap(hovered => {
    //     if(hovered) {
    //       this.rootSubgroupList._dropListRef._startReceiving()
    //     }
    //   })
    // )
  }

  ngAfterViewInit() {
    console.log('rootDropListOrientation: ', this.rootOrientation);
  }

  rootEnterPredicate = (dragData: CdkDrag, dropList: CdkDropList): boolean => {
    const isSubgroup = ('parentSubgroupId' in dragData.data);
    const isHovered = this.dropListRegistry.targetBoundingContainer$.getValue()?.entityType === 'root';

    // console.log('root pred result: ', (isSubgroup && isHovered));
    return isSubgroup && isHovered;
  }


  getClientRect() {
    console.log(this.rootContainerRegistrationRef.element.nativeElement.getBoundingClientRect());
  }

  resetAfterDragReleased(event: CdkDragRelease) {
    this.rootSubgroupList.sortingDisabled = false;

    setTimeout(() => this.dropListRegistry.resetAfterDragEnd(), 300);
  }

  toggleDropListOrientation() {
    this.reorientingDropList = true;
    const current = this.groupManagementUiPrefs.getRootDropListOrientation;

    if(current === 'horizontal') {
      this.groupManagementUiPrefs.setRootDropListOrientation('vertical');
    } else {
      this.groupManagementUiPrefs.setRootDropListOrientation('horizontal');
    }

    this.rootOrientation = this.groupManagementUiPrefs.getRootDropListOrientation;

    setTimeout(() => this.reorientingDropList = false, 1000);
  }

  toggleCollapseAll() {
    this.collapseAll$.next(!this.collapseAll$.getValue());
    this.collapseRootChildrenNotRoots$.next(true);
  }

  toggleCollapseRootsChildren() {
    if(this.collapseAll$.getValue()) {
      this.collapseAll$.next(false);
    }
    this.collapseRootChildrenNotRoots$.next(!this.collapseRootChildrenNotRoots$.getValue())
  }

  ngOnDestroy() {
    this.subgroupInteract.clearTrees();
    // this.dropListRegistry.unregisterList(this.rootListRegistrationRef);
    // this.dropListRegistry.unregisterContainer(this.rootContainerRegistrationRef);
    // this.dropListRegistry.unregisterHoverTarget(this.rootHoverTargetRegistrationRef);
    this.dropListRegistry.clearAllRegisteredLists();

    this.destroy$.next();
    this.destroy$.complete();
  }

}
