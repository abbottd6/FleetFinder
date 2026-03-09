import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterModule } from '@angular/router';
import { of } from 'rxjs';

import { UserAcctListingsTableComponent } from './user-acct-listings-table.component';
import { ListingOwnerActionsService } from '../../services/facade-services/listing-view-interactions/listing-owner-actions.service';
import { AuthService } from '../../services/auth/auth-services/auth.service';
import { ChatHostService } from '../../services/facade-services/chat/chat-host.service';
import { UiPrefsService } from '../../services/facade-services/ui-prefs/ui-prefs.service';
import { QuickAccessMenuService } from '../../services/component-services/quick-access-menu/quick-access-menu.service';
import { UserService } from '../../services/user-services/user.service';
import { ListingViewInteractionsService } from '../../services/facade-services/listing-view-interactions/listing-view-interactions.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';

describe('UserAcctListingsTableComponent', () => {
  let component: UserAcctListingsTableComponent;
  let fixture: ComponentFixture<UserAcctListingsTableComponent>;

  beforeEach(async () => {
    const ownerActionsSpy = jasmine.createSpyObj('ListingOwnerActionsService',
      ['userUpdateSelected', 'confirmDelete']);
    const userServiceSpy = jasmine.createSpyObj('UserService', [], {
      sessionUser$: of(null),
      userId: 0
    });
    const listingInteractSpy = jasmine.createSpyObj('ListingViewInteractionsService',
      ['setSelectedListing', 'userIsListingOwner'], {
        selectedListing$: of(null),
        refresh$: of(null),
        bookmarkedIds$: of(new Set())
      });
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    const dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
    const authSpy = jasmine.createSpyObj('AuthService', ['logout'], { isLoggedIn$: of(false) });
    const chatHostSpy = jasmine.createSpyObj('ChatHostService', ['openChatWith', 'toggleChat'], { open$: of(false) });
    const uiPrefsSpy = jasmine.createSpyObj('UiPrefsService', ['loadUiPrefs', 'saveRowClick'], {
      uiPrefs: { clickedRowIds: new Set(), hideHiddenListingHint: false, hideBookmarkedListingHint: false }
    });
    const quickMenuSpy = jasmine.createSpyObj('QuickAccessMenuService', ['openMenu', 'registerMenu'], { longPressTriggered: false });

    await TestBed.configureTestingModule({
      imports: [UserAcctListingsTableComponent, HttpClientTestingModule, RouterModule.forRoot([])],
      providers: [
        { provide: ListingOwnerActionsService, useValue: ownerActionsSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: ListingViewInteractionsService, useValue: listingInteractSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: MatDialog, useValue: dialogSpy },
        { provide: AuthService, useValue: authSpy },
        { provide: ChatHostService, useValue: chatHostSpy },
        { provide: UiPrefsService, useValue: uiPrefsSpy },
        { provide: QuickAccessMenuService, useValue: quickMenuSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(UserAcctListingsTableComponent);
    component = fixture.componentInstance;
    component.userListings = [];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('listingForModal EventEmitter should be defined', () => {
    expect(component.listingForModal).toBeDefined();
  });
});
