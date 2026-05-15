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
  debounceTime, delay,
  distinctUntilChanged, filter,
  Observable,
  Subject,
  take,
  takeUntil
} from "rxjs";
import {
  DropListRegistration, DropListRegistryService,
  ElementContainerRegistration, SubgroupHoverTargetRegistration
} from "../../../../services/facade-services/group-management/drop-list-registry.service";
import {CdkDrag, CdkDragHandle, CdkDragRelease, CdkDropList, CdkDropListGroup} from "@angular/cdk/drag-drop";
import {UserService} from "../../../../services/user-services/user.service";
import {Router} from "@angular/router";
import {
  MemberManagementApiService
} from "../../../../services/api-services/group-management/member-management-api.service";
import {
  GroupManagementInteractService
} from "../../../../services/facade-services/group-management/group-management-interact.service";
import {
  SubgroupManagementInteractService
} from "../../../../services/facade-services/group-management/subgroup-management-interact.service";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {CrewSubgroupComponent} from "../crew-subgroup/crew-subgroup.component";
import {MatIcon} from "@angular/material/icon";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatTooltip} from "@angular/material/tooltip";
import {map, tap} from "rxjs/operators";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

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
    CdkDropListGroup
  ],
  templateUrl: './root-subgroup.component.html',
  styleUrl: './root-subgroup.component.css'
})
export class RootSubgroupComponent implements OnInit, AfterViewInit, OnDestroy {
  private destroy$ = new Subject<void>();

  protected readonly dropListRegistry = inject(DropListRegistryService)

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

  protected isHoveredTarget$ = combineLatest([
    this.dropListRegistry.hoveredTargetId$,
    this.rootDropListId$
  ]).pipe(
    map(([hoveredId, rootId]) => hoveredId === rootId)
  )

  protected displayListEntryBlocker$: Observable<boolean> =  combineLatest([
    this.dropListRegistry.isDragging$,
    this.dropListRegistry.dropDataType$,
    this.isHoveredTarget$
  ]).pipe(
    map(([dragging, dataType, isHovered]) =>
      dragging && (dataType === 'subgroup') && !isHovered),
  )

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
    // console.log(this.dropListRegistry.droppableSubgroupLists);
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
