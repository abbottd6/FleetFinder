import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {debounceTime, distinctUntilChanged, shareReplay, Subject, take, takeUntil} from "rxjs";
import {UserService} from "../../services/user-services/user.service";
import {ActivatedRoute, Router} from "@angular/router";
import {MemberManagementApiService} from "../../services/api-services/group-management/member-management-api.service";
import {HttpErrorResponse} from "@angular/common/http";
import {AsyncPipe, NgForOf, NgIf, SlicePipe} from "@angular/common";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {RosterManagementComponent} from "./roster-management/roster-management.component";
import {
  GroupManagementInteractService
} from "../../services/facade-services/group-management/group-management-interact.service";
import {
  GroupMembershipViewModel
} from "../../models/group-management-models/view-models/group-membership/group-membership-view-model";
import {MatIcon} from "@angular/material/icon";
import {
  LoadCrewTemplateFormComponent
} from "./subgroup-management/load-crew-template/load-crew-template-form.component";
import {
  SubgroupManagementInteractService
} from "../../services/facade-services/group-management/subgroup-management-interact.service";
import {
  CrewTemplateViewModel
} from "../../models/group-management-models/view-models/group-composition/crew-template-view-model";
import {CrewSubgroupComponent} from "./subgroup-management/crew-subgroup/crew-subgroup.component";
import {CdkDrag, CdkDropList, DragDropModule} from "@angular/cdk/drag-drop";
import {
  DropListRegistration,
  DropListRegistryService,
  ElementContainerRegistration
} from "../../services/facade-services/group-management/drop-list-registry.service";
import {
  GroupCompSubgroupViewModel
} from "../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";

@Component({
  selector: 'app-group-management-page',
  standalone: true,
  templateUrl: './group-management-page.component.html',
  imports: [
    NgIf,
    MatProgressSpinner,
    RosterManagementComponent,
    SlicePipe,
    MatIcon,
    LoadCrewTemplateFormComponent,
    AsyncPipe,
    CrewSubgroupComponent,
    NgForOf,
    CdkDropList,
    DragDropModule
  ],
  styleUrl: './group-management-page.component.css'
})
export class GroupManagementPageComponent implements OnInit, AfterViewInit, OnDestroy {
  private destroy$ = new Subject<void>();

  //for page size calculation
  @ViewChild('managementContainer') managementContainer!: ElementRef;

  @ViewChild('rootSubgroupList') rootSubgroupList!: CdkDropList;
  @ViewChild('rootSubgroupListElement', {read: ElementRef }) rootSubgroupListElement!: ElementRef<HTMLElement>;
  @ViewChild('groupCompRootContainer', {read: ElementRef }) groupCompRootContainer!: ElementRef<HTMLElement>;

  protected containerHeight!: string;
  protected pageIsLoading: boolean = true;

  protected listingTitle!: string;
  protected groupId!: number;

  protected createFromIsExpanding: boolean = false;
  protected doNotShowCreateFromTemplateForm: boolean = true;
  protected doNotShowSaveTemplateForm: boolean = true;

  protected rootContainerRef!: ElementContainerRegistration;
  protected rootListRef!: DropListRegistration;
  protected connectedToSubgroups: CdkDropList[] = [];
  protected disableRootSubgroupList: boolean = true;

  constructor(private userService: UserService,
              private router: Router,
              protected memberManagementApi: MemberManagementApiService,
              protected managementInteract: GroupManagementInteractService,
              protected subgroupMgmtInteract: SubgroupManagementInteractService,
              private route: ActivatedRoute,
              protected dropListRegistry: DropListRegistryService,
              private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.pageIsLoading = true;
    if(!this.userService.userLoggedIn) {
      this.router.navigateByUrl('');
    }

    const stringId = this.route.snapshot.paramMap.get('groupId');

    this.managementInteract.sessionManager = history.state?.membership as GroupMembershipViewModel | undefined;

    this.groupId = Number(stringId);

    if(!this.groupId || (this.groupId !== this.managementInteract.sessionManager?.listing.groupId)) {
      this.router.navigateByUrl('/nothing-here-page')
      return;
    } else if (!(this.managementInteract.sessionManager?.isAuthorizedManager)) {
      this.router.navigateByUrl('/user-account');
      alert('You are not authorized to manage that group.');
    }

    this.memberManagementApi.verifyGroupManagementAuthorization(this.groupId).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: response => {
          if(response) {
            return;
          }
        },
        error: (e: HttpErrorResponse) => {
          if(e.status === 401) {
            this.router.navigateByUrl('/user-account');
            alert('You are not authorized to manage that group.');
          } else {
            this.router.navigateByUrl('/user-account');
            alert('There was an error retrieving this listing. ' +
              'Please try again later or submit a ticket for help.');
          }
        }
    });

    this.dropListRegistry.hoveredList$.pipe(
      takeUntil(this.destroy$),
      distinctUntilChanged((a, b) => a?.id === b?.id),
      debounceTime(100))
      .subscribe(hovered => {
          this.disableRootSubgroupList = hovered?.id !== this.rootListRef.id;
        }
      )

    this.listingTitle = this.managementInteract.sessionManager.listing.listingTitle;

    this.subgroupMgmtInteract.getExistingSubgroupTrees(this.groupId);

    this.pageIsLoading = false;
    this.cdr.detectChanges();
  }

  ngAfterViewInit() {
    const top = this.managementContainer.nativeElement.getBoundingClientRect().top;
    this.containerHeight = `calc(98vh - ${top}px)`;

    this.rootListRef = this.dropListRegistry.registerList('content-root', 'root',
      this.rootSubgroupList, this.rootSubgroupListElement, 0, undefined);

    this.rootContainerRef = this.dropListRegistry.registerContainer(this.rootListRef?.id, 'root',
      [this.rootListRef.dropList], this.groupCompRootContainer, 0, undefined);

    this.dropListRegistry.allSubgroupLists$.pipe(takeUntil(this.destroy$))
      .subscribe(lists => {
        this.connectedToSubgroups = lists.filter(l => l.id !== this.rootSubgroupList?.id);
      })

  }

  createFromTemplate(template: CrewTemplateViewModel) {
    this.subgroupMgmtInteract.createSubgroupFromTemplate(this.groupId, template);

    setTimeout(() => this.toggleDoNotShowCreateFrom(), 300);
  }

  toggleDoNotShowCreateFrom() {
    this.createFromIsExpanding = true;
    this.doNotShowCreateFromTemplateForm = !this.doNotShowCreateFromTemplateForm;
    setTimeout(() => this.createFromIsExpanding = false, 500);
  }

  toggleDoNotShowSaveAsForm() {
    this.doNotShowSaveTemplateForm = !this.doNotShowSaveTemplateForm;
  }

  // canEnterRoot = (drag: CdkDrag, drop: CdkDropList) => {
  //   let rootHovered: boolean = false;
  //
  //   const hoveredList = this.dropListRegistry.hoveredList$.getValue()?.dropList;
  //   // const hoveredContainer = this.dropListRegistry.hoveredContainer$.getValue()?.
  //
  //   console.log('hoveredListId: ' + hoveredList?.id +', dropId: ' + drop.id)
  //
  //   if((hoveredList?.id === drop.id)) {
  //     this.disableRootSubgroupList = false;
  //     rootHovered = true;
  //   }
  //
  //   const can = 'crewPositions' in drag.data && rootHovered;
  //
  //   console.log('canEnterRoot: ', can);
  //
  //   return can;
  // }


  ngOnDestroy(): void {
    // this.dropListRegistry.unregisterList();

    this.destroy$.next();
    this.destroy$.complete();
  }
}
