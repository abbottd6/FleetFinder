import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of, NEVER, Subject } from 'rxjs';
import { GroupListingsComponent } from './group-listings.component';
import { GroupListingFetchService } from '../../services/api-services/group-listings-fetch-api/group-listing-fetch.service';
import { FilterService } from '../../services/api-services/filter-api/filter.service';
import { ListingViewInteractionsService } from '../../services/facade-services/listing-view-interactions/listing-view-interactions.service';
import { UiPrefsService } from '../../services/facade-services/ui-prefs/ui-prefs.service';
import { QuickAccessMenuService } from '../../services/component-services/quick-access-menu/quick-access-menu.service';
import { AuthService } from '../../services/auth/auth-services/auth.service';
import { UserService } from '../../services/user-services/user.service';
import { ListingOwnerActionsService } from '../../services/facade-services/listing-view-interactions/listing-owner-actions.service';
import { ChatHostService } from '../../services/facade-services/chat/chat-host.service';
import { MatMenuModule } from '@angular/material/menu';

const nullFilterState = { searchInput: '', server: null, environment: null, experience: null,
  category: null, subcategory: null, legality: null, pvpStatus: null, planetarySystem: null,
  planetMoon: null, groupStatus: null, language: null };

describe('GroupListingsComponent', () => {
  let component: GroupListingsComponent;
  let fixture: ComponentFixture<GroupListingsComponent>;

  beforeEach(async () => {
    const glsSpy = jasmine.createSpyObj('GroupListingFetchService', ['searchGroupListings'], {});
    glsSpy.searchGroupListings.and.returnValue(of({ content: [], page: { totalElements: 0, size: 25, number: 0, totalPages: 0 } }));

    const filterSpy = jasmine.createSpyObj('FilterService', ['pushStoredState', 'pullState', 'update', 'clearFilters', 'applyFilters', 'toPersistedState'], {
      state$: of(nullFilterState)
    });
    filterSpy.pullState.and.returnValue(nullFilterState);
    filterSpy.pushStoredState.and.stub();
    filterSpy.toPersistedState.and.returnValue({});

    const listingInteractSpy = jasmine.createSpyObj('ListingViewInteractionsService',
      ['setSelectedListing', 'userIsListingOwner'],
      { selectedListing$: of(null), refresh$: of(null), bookmarkedIds$: of(new Set()), isLoggedIn: false });
    listingInteractSpy.userIsListingOwner = jasmine.createSpy('userIsListingOwner').and.returnValue(false);

    const uiPrefsSpy = jasmine.createSpyObj('UiPrefsService', ['loadUiPrefs', 'saveRowClick', 'clickedCleanupCheck', 'saveUiPrefs'], {
      uiPrefs: { clickedRowIds: new Set(), storedFilters: {}, hideHiddenListingHint: false, hideBookmarkedListingHint: false }
    });
    uiPrefsSpy.loadUiPrefs.and.returnValue({ clickedRowIds: new Set(), storedFilters: {}, hideHiddenListingHint: false, hideBookmarkedListingHint: false });

    const quickMenuSpy = jasmine.createSpyObj('QuickAccessMenuService', ['openMenu'], { longPressTriggered: false });
    const authSpy = jasmine.createSpyObj('AuthService', [], { isLoggedIn$: of(false) });
    const userSpy = jasmine.createSpyObj('UserService', ['refreshUser'], { sessionUser$: of(null) });
    const ownerSpy = jasmine.createSpyObj('ListingOwnerActionsService', ['userUpdateSelected', 'confirmDelete']);
    const chatHostSpy = jasmine.createSpyObj('ChatHostService', ['openChatWith', 'toggleChat'], { open$: of(false) });

    await TestBed.configureTestingModule({
      declarations: [GroupListingsComponent],
      imports: [ MatMenuModule ],
      providers: [
        { provide: GroupListingFetchService, useValue: glsSpy },
        { provide: FilterService, useValue: filterSpy },
        { provide: ListingViewInteractionsService, useValue: listingInteractSpy },
        { provide: UiPrefsService, useValue: uiPrefsSpy },
        { provide: QuickAccessMenuService, useValue: quickMenuSpy },
        { provide: AuthService, useValue: authSpy },
        { provide: UserService, useValue: userSpy },
        { provide: ListingOwnerActionsService, useValue: ownerSpy },
        { provide: ChatHostService, useValue: chatHostSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    spyOn(GroupListingsComponent.prototype, 'ngAfterViewInit').and.stub();
    fixture = TestBed.createComponent(GroupListingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
