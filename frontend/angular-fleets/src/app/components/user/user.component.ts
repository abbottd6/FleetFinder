import {
  Component,
  inject, OnDestroy,
  OnInit, ViewChild,
} from '@angular/core';
import {AuthService} from "../../services/auth/auth-services/auth.service";
import {map, shareReplay, Subject, takeUntil} from "rxjs";
import {Router, RouterModule} from "@angular/router";
import {CommonModule} from "@angular/common";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatListItem, MatNavList} from "@angular/material/list";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import { BreakpointObserver } from "@angular/cdk/layout";
import {UserAcctListingsTableComponent} from "../user-acct-listings-table/user-acct-listings-table.component";
import {MatButtonModule} from "@angular/material/button";
import {SessionUser, UserRole, UserService} from "../../services/user-services/user.service";
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
import {HttpStatusCode} from "@angular/common/http";
import {
  ConfirmDelinkDiscordPopupComponent
} from "../pop-ups/confirm-delink-discord-popup/confirm-delink-discord-popup.component";
import {
  ProfileNotificationsTabComponent
} from "../user-profile-notification-settings-tab/profile-notifications-tab.component";

@Component({
    selector: 'app-user',
    templateUrl: './user.component.html',
    styleUrls: [
      './user.component.css',
    ],
  imports: [CommonModule, RouterModule, MatSidenavModule, MatNavList, MatListItem,
    UserAcctListingsTableComponent, MatButtonModule, GroupListingModalComponent,
    UserProfileBookmarksComponent, ModParentPanelComponent, UserProfileTemplatesComponent, ListingTemplateModalComponent,
    DropdownModule, FormsModule, MatError, MatFormField, MatHint, MatInput, MatLabel, MatFormField, ReactiveFormsModule, ProfileNotificationsTabComponent],
    standalone: true
})
export class UserComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  private breakpointObserver = inject(BreakpointObserver);

  @ViewChild('bookmarks') bookmarks!: UserProfileBookmarksComponent;

  //modal popup vars
  selectedListing: GroupListingViewModel | null = null;
  selectedTemplate: ListingTemplateViewModel | null = null;

  groupListings: GroupListingViewModel[] = []
  selectedTab: 'listings' | 'notifications' |'bookmarks'|'templates'|'profile'|'content_mod' = 'listings';
  shouldDisplayMod$: boolean = false;

  protected editing: boolean = false;

  constructor(public userService: UserService,
              protected auth: AuthService,
              protected listingInteract: ListingViewInteractionsService,
              protected templatesModal: TemplatesModalService,
              private chatHostSrv: ChatHostService,
              private userApiSrv: UserApiService,
              private router: Router,
              private dialog: MatDialog,
              protected userFormSrv: UpdateUserFormService) {

    this.listingInteract.refresh$.pipe(takeUntil(this.destroy$)).subscribe( reason => {
      if(reason != null) {
        this.userService.refreshUser();
      }
    })
  }

  ngOnInit() {
    this.userService.refreshUser();
    this.shouldDisplayMod$ = this.askShouldDisplayMod();

    this.userService.sessionUser$.pipe(
      map(user => user?.groupListingsDto ?? []),
      takeUntil(this.destroy$)
    ).subscribe(listings => this.groupListings = listings);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  askShouldDisplayMod(): boolean {
    return this.userService.primaryRole == UserRole.mod;
  }

  selectTab(tab: typeof this.selectedTab){
    this.selectedTab = tab;
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

  isMobile$ = this.breakpointObserver
    .observe('(max-width: 1200px)')
    .pipe(map(result => result.matches),
      shareReplay());
}
