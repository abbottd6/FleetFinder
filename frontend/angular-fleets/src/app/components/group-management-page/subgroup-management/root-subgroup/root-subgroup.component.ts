import {
  AfterViewInit, ChangeDetectorRef,
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
  combineLatest, distinctUntilChanged,
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
  GroupCompositionInteractService
} from "../../../../services/facade-services/group-management/group-composition-interact.service";
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
import {toTitleCase} from "../../../../utils/global-functions";
import {
  GroupCompSubgroupViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";
import {ConfirmGenericComponent} from "../../../pop-ups/confirm-generic/confirm-generic.component";
import {MatDialog} from "@angular/material/dialog";
import {ManagementRsvpService} from "../../../../services/facade-services/group-management/management-rsvp.service";
import {GroupListingViewModel} from "../../../../models/group-listing/group-listing-view-model";

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
    NgIf,
    MatMenuTrigger,
    CdkDropListGroup,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
  ],
  templateUrl: './root-subgroup.component.html',
  styleUrl: './root-subgroup.component.css'
})
export class RootSubgroupComponent implements OnInit, AfterViewInit, OnDestroy {
  private destroy$ = new Subject<void>();

  protected readonly dropListRegistry = inject(DropListRegistryService);
  protected readonly groupManagementUiPrefs = inject(GroupManagementUiPrefsService);

  @Input() groupListing!: GroupListingViewModel;

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
  protected togglesNextOrientation!: DropListOrientation;

  //horizontal orientation only
  protected canScrollHorizontal$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  protected showLeftScroll: boolean = false;
  protected showRightScroll: boolean = false;

  protected isHoveredTarget$ = combineLatest([
    this.dropListRegistry.hoveredTargetId$,
    this.rootDropListId$
  ]).pipe(
    map(([hoveredId, rootId]) => hoveredId === rootId)
  )

  protected allExpanded$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  protected rootChildrenExpanded$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);

  constructor(protected compositionInteract: GroupCompositionInteractService,
              protected mgmtRsvpService: ManagementRsvpService,
              private dialog: MatDialog){
  }

  ngOnInit() {
    if(this.rootOrientation === 'horizontal') {
      this.togglesNextOrientation = 'vertical';
    } else if(this.rootOrientation === 'vertical') {
      this.togglesNextOrientation = 'mixed';
    } else if(this.rootOrientation === 'mixed') {
      this.togglesNextOrientation = 'horizontal';
    }
  }

  ngAfterViewInit() {
    this.compositionInteract.subgroupTrees$.pipe(
      filter(trees => trees?.length > 0),
      take(1),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      Promise.resolve().then(() => {
        this.rootListRegistrationRef = this.dropListRegistry.registerList('content-root', 'root',
          this.rootSubgroupList, this.rootSubgroupListElement, 0, 'root');

        this.rootDropListId$.next(this.rootSubgroupList.id);

        this.rootContainerRegistrationRef = this.dropListRegistry.registerContainer(this.rootListRegistrationRef.id, 'root',
          [this.rootSubgroupList], this.groupCompRootContainer, 0, 'content-root');

        this.rootHoverTargetRegistrationRef = this.dropListRegistry.registerHoverTarget(this.rootSubgroupList.id,
          this.rootSubgroupList, this.rootListHoverTarget, this.rootContainerRegistrationRef, 0);
      }).then(() => {
        this.dropListRegistry.allSubgroupLists$.pipe(takeUntil(this.destroy$))
          .subscribe(registeredLists => {
            this.connectedToSubgroups = registeredLists.filter(l => l.id !== this.rootSubgroupList?.id)
              .map(regList => regList.dropList);
          })
      })
    })

    this.updateHorizontalScrollButtonVisibility();
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
    this.compositionInteract.reorientingDropList = true;
    const current = this.groupManagementUiPrefs.getRootDropListOrientation;

    if(current === 'horizontal') {
      this.groupManagementUiPrefs.setRootDropListOrientation('vertical');
      this.rootOrientation = 'vertical';
      this.togglesNextOrientation = 'mixed';
      this.canScrollHorizontal$.next(false);
    } else if(current === 'vertical') {
      this.groupManagementUiPrefs.setRootDropListOrientation('mixed');
      this.rootOrientation = 'mixed';
      this.togglesNextOrientation = 'horizontal';
      this.canScrollHorizontal$.next(false);
    } else {
      this.groupManagementUiPrefs.setRootDropListOrientation('horizontal')
      this.rootOrientation = 'horizontal'
      this.togglesNextOrientation = 'vertical'
      this.canScrollHorizontal$.next(true);
    }

    setTimeout(() => {
      this.updateHorizontalScrollButtonVisibility();
      this.compositionInteract.reorientingDropList = false
    }, 500);
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

          this.compositionInteract.deleteRootLevelSubgroup(forDelete);
        }
      });
  }

  toggleCollapseAll() {
    this.allExpanded$.next(!this.allExpanded$.getValue());
    this.rootChildrenExpanded$.next(this.allExpanded$.getValue());
  }

  toggleCollapseRootsChildren() {
    if(!this.allExpanded$.getValue()) {
      this.allExpanded$.next(true);
    }
    this.rootChildrenExpanded$.next(!this.rootChildrenExpanded$.getValue())
  }

  scrollHorizontal(dir: 'left' | 'right') {
    const scrollSegment = this.rootSubgroupListElement.nativeElement.clientWidth * 0.5;

    this.rootSubgroupListElement.nativeElement.scrollBy({left: dir === 'right' ? scrollSegment : -scrollSegment, behavior: 'smooth'});
  }

  updateHorizontalScrollButtonVisibility() {
    const el = this.rootSubgroupListElement.nativeElement;
    const max = el.scrollWidth - el.clientWidth;
    this.canScrollHorizontal$.next((max > 1) && (this.rootOrientation === 'horizontal'));
    this.showLeftScroll = el.scrollLeft > 5;
    this.showRightScroll = el.scrollLeft < max - 1;
  }

  extractCompositionAndEmitTemplateSave() {
    const root = this.compositionInteract.getSubgroupTreesSnapshot();
    this.compositionInteract.saveCompositionAsTemplate('root', root);
  }


  ngOnDestroy() {
    // this.subgroupInteract.clearTrees();
    if(this.rootListRegistrationRef) {
      this.dropListRegistry.unregisterList(this.rootListRegistrationRef);
      this.dropListRegistry.unregisterContainer(this.rootContainerRegistrationRef);
      this.dropListRegistry.unregisterHoverTarget(this.rootHoverTargetRegistrationRef);
    }
    // this.dropListRegistry.clearAllRegisteredLists();

    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly toTitleCase = toTitleCase;
}
