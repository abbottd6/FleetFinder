import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { UserProfileBookmarksComponent } from './user-profile-bookmarks.component';
import { BookmarkApiService } from '../../services/api-services/bookmarks-api/bookmark-api.service';
import { AuthService } from '../../services/auth/auth-services/auth.service';
import { ChatHostService } from '../../services/facade-services/chat/chat-host.service';
import { UiPrefsService } from '../../services/facade-services/ui-prefs/ui-prefs.service';
import { QuickAccessMenuService } from '../../services/component-services/quick-access-menu/quick-access-menu.service';
import { ListingViewInteractionsService } from '../../services/facade-services/listing-view-interactions/listing-view-interactions.service';
import { ListingOwnerActionsService } from '../../services/facade-services/listing-view-interactions/listing-owner-actions.service';

describe('UserBookmarksTableComponent', () => {
  let component: UserProfileBookmarksComponent;
  let fixture: ComponentFixture<UserProfileBookmarksComponent>;

  beforeEach(async () => {
    const bookmarkApiSpy = jasmine.createSpyObj('BookmarkApiService', ['getBookmarks', 'deleteBookmark']);
    bookmarkApiSpy.getBookmarks.and.returnValue(of({
      content: [],
      page: { totalElements: 0, size: 10, number: 0, totalPages: 0 }
    }));

    const authSpy = jasmine.createSpyObj('AuthService', ['logout'], { isLoggedIn$: of(false) });
    const chatHostSpy = jasmine.createSpyObj('ChatHostService', ['openChatWith', 'toggleChat'], { open$: of(false) });
    const uiPrefsSpy = jasmine.createSpyObj('UiPrefsService', ['loadUiPrefs', 'saveRowClick'], {
      uiPrefs: { clickedRowIds: new Set(), hideHiddenListingHint: false, hideBookmarkedListingHint: false }
    });
    const quickMenuSpy = jasmine.createSpyObj('QuickAccessMenuService', ['openMenu', 'registerMenu'], { longPressTriggered: false });
    const listingInteractSpy = jasmine.createSpyObj('ListingViewInteractionsService',
      ['setSelectedListing', 'userIsListingOwner'], {
        selectedListing$: of(null),
        refresh$: of(null),
        bookmarkedIds$: of(new Set())
      });
    const ownerActionsSpy = jasmine.createSpyObj('ListingOwnerActionsService',
      ['userUpdateSelected', 'confirmDelete']);

    await TestBed.configureTestingModule({
      imports: [UserProfileBookmarksComponent, HttpClientTestingModule],
      providers: [
        { provide: BookmarkApiService, useValue: bookmarkApiSpy },
        { provide: ListingViewInteractionsService, useValue: listingInteractSpy },
        { provide: AuthService, useValue: authSpy },
        { provide: ChatHostService, useValue: chatHostSpy },
        { provide: UiPrefsService, useValue: uiPrefsSpy },
        { provide: QuickAccessMenuService, useValue: quickMenuSpy },
        { provide: ListingOwnerActionsService, useValue: ownerActionsSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(UserProfileBookmarksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('listingForModal EventEmitter should be defined', () => {
    expect(component.listingForModal).toBeDefined();
  });
});
