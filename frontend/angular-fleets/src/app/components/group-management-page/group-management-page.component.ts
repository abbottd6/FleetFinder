import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild, ViewChildren} from '@angular/core';
import {Subject, takeUntil} from "rxjs";
import {
  GroupManagementMemberViewModel
} from "../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
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
import {CdkDropList, DragDropModule} from "@angular/cdk/drag-drop";
import {DropListRegistryService} from "../../services/facade-services/group-management/drop-list-registry.service";

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

  @ViewChild('managementContainer') managementContainer!: ElementRef;
  protected containerHeight!: string;

  protected listingTitle!: string;

  protected pageIsLoading: boolean = true;

  protected createFromIsExpanding: boolean = false;
  protected doNotShowCreateFromTemplateForm: boolean = true;

  protected doNotShowSaveTemplateForm: boolean = true;

  protected groupId!: number;

  @ViewChild('rootSubgroupList') rootSubgroupList!: CdkDropList;

  constructor(private userService: UserService,
              private router: Router,
              protected memberManagementApi: MemberManagementApiService,
              protected managementInteract: GroupManagementInteractService,
              protected subgroupMgmtInteract: SubgroupManagementInteractService,
              private route: ActivatedRoute,
              private dropListRegistry: DropListRegistryService) {}

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

    this.listingTitle = this.managementInteract.sessionManager.listing.listingTitle;

    this.subgroupMgmtInteract.getExistingSubgroupTrees(this.groupId);

    this.pageIsLoading = false;
  }

  ngAfterViewInit() {
    const top = this.managementContainer.nativeElement.getBoundingClientRect().top;
    this.containerHeight = `calc(98vh - ${top}px)`;

    // this.dropListRegistry.registerList(this.rootSubgroupList);
    // console.log('registered list:', this.rootSubgroupList.element.nativeElement, 'id:', this.rootSubgroupList.id);
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

  ngOnDestroy(): void {
    this.dropListRegistry.unregisterList(this.rootSubgroupList);

    this.destroy$.next();
    this.destroy$.complete();
  }
}
