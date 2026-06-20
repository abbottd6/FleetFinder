import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  inject,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';
import {filter, Observable, Subject, takeUntil} from "rxjs";
import {UserService} from "../../services/user-services/user.service";
import {ActivatedRoute, Router} from "@angular/router";
import {MemberManagementApiService} from "../../services/api-services/group-management/member-management-api.service";
import {HttpErrorResponse} from "@angular/common/http";
import {AsyncPipe, NgIf, SlicePipe} from "@angular/common";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {RosterManagementComponent} from "./roster-management/roster-management.component";
import {
  GroupManagementInteractService
} from "../../services/facade-services/group-management/group-management-interact.service";
import {
  GroupMembershipViewModel
} from "../../models/group-management-models/view-models/group-membership/group-membership-view-model";
import {
  LoadCrewTemplateFormComponent
} from "./subgroup-management/load-crew-template/load-crew-template-form.component";
import {
  SubgroupManagementInteractService
} from "../../services/facade-services/group-management/subgroup-management-interact.service";
import {
  CrewTemplateViewModel
} from "../../models/group-management-models/view-models/group-composition/crew-template-view-model";
import {DragDropModule} from "@angular/cdk/drag-drop";
import {
  DropListRegistryService,
} from "../../services/facade-services/group-management/drop-list-registry.service";

import {RootSubgroupComponent} from "./subgroup-management/root-subgroup/root-subgroup.component";
import {map} from "rxjs/operators";
import {MatIcon} from "@angular/material/icon";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {
  MgmtMemberQuickAccessMenuService
} from "../../services/component-services/group-management-quick-access-menus/mgmt-member-quick-access-menu.service";

@Component({
  selector: 'app-group-management-page',
  standalone: true,
  templateUrl: './group-management-page.component.html',
  imports: [
    NgIf,
    MatProgressSpinner,
    RosterManagementComponent,
    LoadCrewTemplateFormComponent,
    DragDropModule,
    RootSubgroupComponent,
    AsyncPipe,
    MatIcon,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger
  ],
  styleUrl: './group-management-page.component.css',
  providers: [DropListRegistryService, SubgroupManagementInteractService]
})
export class GroupManagementPageComponent implements OnInit, AfterViewInit, OnDestroy {
  private destroy$ = new Subject<void>();
  protected readonly managementInteract = inject(GroupManagementInteractService);

  //for page size calculation
  @ViewChild('managementContainer') managementContainer!: ElementRef;

  @ViewChild(MatMenuTrigger) menuTrigger!: MatMenuTrigger;
  @ViewChild('memberContextMenuAnchor', { read: ElementRef }) protected memberContextMenuAnchor!: ElementRef<HTMLElement>;

  protected containerHeight!: string;

  protected listingTitle!: string;

  protected createFromIsExpanding: boolean = false;
  protected showCreateFromTemplate: boolean = false;
  protected doNotShowCreateFromTemplateForm: boolean = true;
  protected doNotShowSaveTemplateForm: boolean = true;

  protected activeMemberCount$: Observable<number> = this.managementInteract.activeRoster$.pipe(
    takeUntil(this.destroy$),
    map(members =>
      (members.content ?? []).length)
  );

  protected waitlistMemberCount$: Observable<number> = this.managementInteract.waitlistRoster$.pipe(
    takeUntil(this.destroy$),
    map(waitlist =>
      (waitlist.content ?? []).length)
  );

  protected pendingInviteCount$: Observable<number> = this.managementInteract.groupInvites$.pipe(
    takeUntil(this.destroy$),
    map(invites =>
      (invites.content ?? []).filter(
        invite => invite.inviteStatus === 'pending'
      ).length)
  )

  constructor(private userService: UserService,
              private router: Router,
              protected memberManagementApi: MemberManagementApiService,
              protected subgroupMgmtInteract: SubgroupManagementInteractService,
              private route: ActivatedRoute,
              protected dropListRegistry: DropListRegistryService,
              protected rosterMemberQuickMenu: MgmtMemberQuickAccessMenuService) {}

  ngOnInit() {
    if(!this.userService.userLoggedIn) {
      this.router.navigateByUrl('');
    }

    this.dropListRegistry.pageDataLoading = true;

    const stringId = this.route.snapshot.paramMap.get('groupId');

    this.managementInteract.sessionManager = history.state?.membership as GroupMembershipViewModel | undefined;

    this.managementInteract.groupId = Number(stringId);

    if(!this.managementInteract.groupId || (this.managementInteract.groupId !== this.managementInteract.sessionManager?.listing.groupId)) {
      this.router.navigateByUrl('/nothing-here-page')
      return;
    } else if (!(this.managementInteract.sessionManager?.isAuthorizedManager)) {
      this.router.navigateByUrl('/user-account');
      alert('You are not authorized to manage that group.');
    }

    this.memberManagementApi.verifyGroupManagementAuthorization(this.managementInteract.groupId).pipe(takeUntil(this.destroy$))
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

    this.subgroupMgmtInteract.getExistingGroupComposition(this.managementInteract.groupId);
  }

  ngAfterViewInit() {
    const top = this.managementContainer.nativeElement.getBoundingClientRect().top;
    this.containerHeight = `calc(98vh - ${top}px)`;

    this.rosterMemberQuickMenu.registerMenu(this.menuTrigger, this.memberContextMenuAnchor);
  }

  createFromTemplate(template: CrewTemplateViewModel) {
    this.subgroupMgmtInteract.createSubgroupFromTemplate(this.managementInteract.groupId, template);

    setTimeout(() => this.toggleDoNotShowCreateFrom(), 300);
  }

  toggleDoNotShowCreateFrom() {
    if(!this.showCreateFromTemplate) {
      this.showCreateFromTemplate = true;
      this.createFromIsExpanding = true;
      setTimeout(() => this.doNotShowCreateFromTemplateForm = false, 120);
      setTimeout(() => this.createFromIsExpanding = false, 500);
    } else {
     this.createFromIsExpanding = true;
     this.doNotShowCreateFromTemplateForm = true;
     setTimeout(() => this.showCreateFromTemplate = false, 500);
     setTimeout(() => this.createFromIsExpanding = false, 500);
    }
  }

  toggleDoNotShowSaveAsForm() {
    this.doNotShowSaveTemplateForm = !this.doNotShowSaveTemplateForm;
  }


  ngOnDestroy(): void {

    this.destroy$.next();
    this.destroy$.complete();
  }
}
