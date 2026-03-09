import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';

import { MobileFeedViewComponent } from './mobile-feed-view.component';
import { MatTableDataSource } from '@angular/material/table';
import { GroupListingViewModel } from '../../../models/group-listing/group-listing-view-model';
import { ListingViewInteractionsService } from '../../../services/facade-services/listing-view-interactions/listing-view-interactions.service';
import { UiPrefsService } from '../../../services/facade-services/ui-prefs/ui-prefs.service';
import { AuthService } from '../../../services/auth/auth-services/auth.service';
import { QuickAccessMenuService } from '../../../services/component-services/quick-access-menu/quick-access-menu.service';
import { ListingOwnerActionsService } from '../../../services/facade-services/listing-view-interactions/listing-owner-actions.service';
import { ChatHostService } from '../../../services/facade-services/chat/chat-host.service';

describe('MobileFeedViewComponent', () => {
  let component: MobileFeedViewComponent;
  let fixture: ComponentFixture<MobileFeedViewComponent>;

  beforeEach(async () => {
    const listingInteractSpy = jasmine.createSpyObj('ListingViewInteractionsService',
      ['setSelectedListing', 'userIsListingOwner'], {
        selectedListing$: of(null),
        bookmarkedIds$: of(new Set()),
        refresh$: of(null)
      });
    listingInteractSpy.userIsListingOwner = jasmine.createSpy('userIsListingOwner').and.returnValue(false);

    const uiPrefsSpy = jasmine.createSpyObj('UiPrefsService',
      ['loadUiPrefs', 'saveRowClick'], {
        uiPrefs: { clickedRowIds: new Set(), hideHiddenListingHint: false, hideBookmarkedListingHint: false }
      });
    uiPrefsSpy.loadUiPrefs.and.returnValue({ clickedRowIds: new Set(), hideHiddenListingHint: false, hideBookmarkedListingHint: false });

    const authSpy = jasmine.createSpyObj('AuthService', [], { isLoggedIn$: of(false) });
    const quickMenuSpy = jasmine.createSpyObj('QuickAccessMenuService', ['registerMenu'], { longPressTriggered: false });
    const ownerActionsSpy = jasmine.createSpyObj('ListingOwnerActionsService', ['userUpdateSelected', 'confirmDelete']);
    const chatHostSpy = jasmine.createSpyObj('ChatHostService', ['openChatWith'], { open$: of(false) });

    await TestBed.configureTestingModule({
      imports: [MobileFeedViewComponent],
      providers: [
        { provide: ListingViewInteractionsService, useValue: listingInteractSpy },
        { provide: UiPrefsService, useValue: uiPrefsSpy },
        { provide: AuthService, useValue: authSpy },
        { provide: QuickAccessMenuService, useValue: quickMenuSpy },
        { provide: ListingOwnerActionsService, useValue: ownerActionsSpy },
        { provide: ChatHostService, useValue: chatHostSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(MobileFeedViewComponent);
    component = fixture.componentInstance;
    component.dataSource = new MatTableDataSource<GroupListingViewModel>([]);
    component.columns = [];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
