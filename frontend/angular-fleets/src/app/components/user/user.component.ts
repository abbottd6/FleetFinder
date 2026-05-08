import {
  AfterViewInit, ChangeDetectorRef,
  Component, ElementRef,
  inject, OnDestroy,
  OnInit, ViewChild,
} from '@angular/core';
import {AuthService} from "../../services/auth/auth-services/auth.service";
import {map, shareReplay, Subject, take, takeUntil} from "rxjs";
import {ActivatedRoute, Router, RouterModule} from "@angular/router";
import {CommonModule} from "@angular/common";
import {MatSidenav, MatSidenavModule} from "@angular/material/sidenav";
import {MatListItem, MatNavList} from "@angular/material/list";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import { BreakpointObserver } from "@angular/cdk/layout";
import {UserAcctListingsTableComponent} from "../user-acct-listings-table/user-acct-listings-table.component";
import {MatButtonModule} from "@angular/material/button";
import {UserRole, UserService} from "../../services/user-services/user.service";
import {CloseValue, GroupListingModalComponent} from "../group-listing-modal/group-listing-modal.component";
import {environment} from "../../../environments/environment";
import {UserProfileBookmarksComponent} from "../user-profile-bookmarks/user-profile-bookmarks.component";
import {
  ListingViewInteractionsService
} from "../../services/facade-services/listing-view-interactions/listing-view-interactions.service";
import {ModParentPanelComponent} from "../mod-tools/mod-parent-panel/mod-parent-panel.component";
import {
  UserProfileTemplatesComponent
} from "../user-profile-templates/user-profile-templates.component";
import {
  TemplatesModalService
} from "../../services/component-services/templates-modal-service/templates-modal.service";
import {ListingTemplateViewModel} from "../../models/listing-templates/listing-template-view-model";
import {
  ListingTemplateModalComponent
} from "../group-listing-modal/listing-template-modal/listing-template-modal.component";
import {ChatHostService} from "../../services/facade-services/chat/chat-host.service";
import {UserApiService} from "../../services/user-services/userApi.service";
import {MatDialog} from "@angular/material/dialog";
import {
  UserDeleteAccountPopupComponent
} from "../pop-ups/user-delete-account-popup/user-delete-account-popup.component";
import {DropdownModule} from "../dropdowns/dropdown-module/dropdown.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {MatError, MatHint} from "@angular/material/form-field";
import {UpdateUserFormService} from "../../services/user-services/update-user-form.service";
import {UpdateUserRequest} from "../../models/private-user/update-user-request";
import {
  ProfileNotificationsTabComponent
} from "../user-profile-notification-settings-tab/profile-notifications-tab.component";
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from "@angular/material/expansion";
import {UserProfileMyGroupsComponent} from "../user-profile-my-groups/user-profile-my-groups.component";

const VALID_TABS = [
  'groups',
  'listings',
  'notifications',
  'bookmarks',
  'templates',
  'profile',
  'content_mod'
] as const;

type ProfileSelectedTab = typeof VALID_TABS[number];

@Component({
    selector: 'app-user',
    templateUrl: './user.component.html',
    styleUrls: [
      './user.component.css',
    ],
  imports: [CommonModule, RouterModule, MatSidenavModule, MatNavList, MatListItem,
    UserAcctListingsTableComponent, MatButtonModule, GroupListingModalComponent,
    UserProfileBookmarksComponent, ModParentPanelComponent, UserProfileTemplatesComponent, ListingTemplateModalComponent,
    DropdownModule, FormsModule, MatError, MatFormField, MatHint, MatInput, MatLabel, MatFormField, ReactiveFormsModule, ProfileNotificationsTabComponent, MatExpansionPanelHeader, MatExpansionPanelTitle, MatExpansionPanel, UserProfileMyGroupsComponent],
    standalone: true
})
export class UserComponent implements OnInit, AfterViewInit, OnDestroy {
  private destroy$ = new Subject<void>();
  private breakpointObserver = inject(BreakpointObserver);

  @ViewChild('bookmarks') bookmarks!: UserProfileBookmarksComponent;
  @ViewChild('drawer') sidenavDrawer!: MatSidenav;

  @ViewChild('profileContainer') profileContainer!: ElementRef;
  protected containerHeight!: string;

  routeSubsection?: string = undefined;

  //modal popup vars
  selectedListing: GroupListingViewModel | null = null;
  selectedTemplate: ListingTemplateViewModel | null = null;

  groupListings: GroupListingViewModel[] = []
  selectedTab: ProfileSelectedTab = 'groups';
  shouldDisplayMod$: boolean = false;

  protected editing: boolean = false;

  constructor(public userService: UserService,
              protected auth: AuthService,
              protected listingInteract: ListingViewInteractionsService,
              protected templatesModal: TemplatesModalService,
              private chatHostSrv: ChatHostService,
              private userApiSrv: UserApiService,
              private dialog: MatDialog,
              protected userFormSrv: UpdateUserFormService,
              private route: ActivatedRoute,
              private router: Router,
              private cdr: ChangeDetectorRef) {

    this.listingInteract.refresh$.pipe(takeUntil(this.destroy$)).subscribe( reason => {
      if(reason != null) {
        this.userService.refreshUser();
      }
    })
  }

  ngOnInit() {
    if(sessionStorage.getItem('pendingDiscordLink')) {
      sessionStorage.removeItem('pendingDiscordLink');
      this.userService.kcProfileRefresh();
    }

    this.userService.refreshUser();
    this.shouldDisplayMod$ = this.askShouldDisplayMod();

    this.userService.sessionUser$.pipe(
      map(user => user?.groupListingsDto ?? []),
      takeUntil(this.destroy$)
    ).subscribe(listings => this.groupListings = listings);

    this.route.paramMap.subscribe(params => {
      const tab = params.get('tab') ?? 'groups';
      if (this.isValidTabParam(tab)) {
        this.selectTab(tab as ProfileSelectedTab);

        this.route.queryParams.subscribe(params => {
          const section = params['section'];
          if(!section) return;
          this.routeSubsection = section;

          setTimeout(() => {
            const url = this.router.url.split(tab)[0];
            this.router.navigateByUrl(url, {replaceUrl: true});
            this.routeSubsection = undefined;
          }, 1000);
        });
      }
    });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  askShouldDisplayMod(): boolean {
    return this.userService.primaryRole == UserRole.mod;
  }

  selectTab(tab: ProfileSelectedTab){
    this.selectedTab = tab;
    this.isMobile$.pipe(take(1)).subscribe(isMobile => {
      if(isMobile && this.selectedTab != 'groups') {
        setTimeout(() => this.sidenavDrawer.toggle(), 300);
      }
    })
  }

  onListingSelected(listing: GroupListingViewModel) {
    this.selectedListing = listing;
    this.listingInteract.onRowClick(listing);
    // this.listingInteract.isModalVisible = true;
    if(!environment.production) {
      console.log("Parent modal visibility: ", this.listingInteract.isModalVisible);
    }
  }

  onTemplateSelected(template: ListingTemplateViewModel) {
    this.selectedTemplate = template;

    if(this.listingInteract.isModalVisible) {
      this.listingInteract.onModalClose({value: null, group: null} as CloseValue);
    }

    history.pushState({templateModal: true}, '');
    this.templatesModal.templateModalIsVisible = true;
  }

  userComponentLogout() {
    this.chatHostSrv.closeChat();
    this.auth.logout().subscribe();
  }

  openConfirmUserDelete(): void {
    const dialogRef = this.dialog.open(UserDeleteAccountPopupComponent);

    dialogRef.afterClosed().subscribe(result => {
      if(result) {
        this.userDelete();
      }
    });
  }

  userDelete() {
    this.userApiSrv.deleteUser().subscribe(
      result => {
        this.userComponentLogout();
      }
    );
  }

  userProfileEdit() {
    this.editing = true;
  }

  cancelEdit() {
    this.editing = false;
  }

  saveProfileEdit() {
    const server = this.userFormSrv.serverControl.value;
    const org = this.userFormSrv.orgControl.value;

    const request = new UpdateUserRequest(server, org);
    this.userApiSrv.updateMe(request).subscribe(response => {
          this.userService.refreshUser();
          this.editing = false;
      }
    )
  }

  isValidTabParam(tab: string | null) {
    return !!tab && VALID_TABS.includes(tab as ProfileSelectedTab);
  }

  ngAfterViewInit() {
    const top = this.profileContainer.nativeElement.getBoundingClientRect().top;
    this.containerHeight = `calc(99vh - ${top}px)`;
    this.cdr.detectChanges();
  }

  isMobile$ = this.breakpointObserver
    .observe('(max-width: 1200px)')
    .pipe(map(result => result.matches),
      shareReplay());
}
