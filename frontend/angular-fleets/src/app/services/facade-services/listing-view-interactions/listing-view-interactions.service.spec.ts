import { TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { of, NEVER } from 'rxjs';

import { ListingViewInteractionsService } from './listing-view-interactions.service';
import { BookmarkApiService } from '../../api-services/bookmarks-api/bookmark-api.service';
import { ListingReportApiService } from '../../api-services/listing-reports-api/listing-report-api.service';
import { HiddenListingsApiService } from '../../api-services/hidden-listings-api/hidden-listings-api.service';
import { UiPrefsService } from '../ui-prefs/ui-prefs.service';
import { UserService } from '../../user-services/user.service';
import { TemplatesModalService } from '../../component-services/templates-modal-service/templates-modal.service';
import { GroupListingViewModel } from '../../../models/group-listing/group-listing-view-model';

describe('ListingViewInteractionsService', () => {
  let service: ListingViewInteractionsService;
  let bmSpy: jasmine.SpyObj<BookmarkApiService>;
  let reportApiSpy: jasmine.SpyObj<ListingReportApiService>;
  let hideSpy: jasmine.SpyObj<HiddenListingsApiService>;
  let uiPrefSpy: jasmine.SpyObj<UiPrefsService>;
  let userSpy: jasmine.SpyObj<UserService>;
  let templatesModalSpy: jasmine.SpyObj<TemplatesModalService>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  beforeEach(() => {
    bmSpy = jasmine.createSpyObj('BookmarkApiService', ['getBookmarksBrief', 'addBookmark', 'deleteBookmark', 'deleteMultipleBookmarks'], {
      'bookmarksBrief$': of([] as number[]),
    });
    bmSpy.getBookmarksBrief.and.returnValue(of([]));

    reportApiSpy = jasmine.createSpyObj('ListingReportApiService', ['submitReport'], {
      'reportOptions$': of([]),
    });

    hideSpy = jasmine.createSpyObj('HiddenListingsApiService', ['addHidden', 'clearHidden', 'undoLastHide']);

    uiPrefSpy = jasmine.createSpyObj('UiPrefsService', [
      'saveRowClick', 'displayBookmarkListingHint', 'displayHideListingHint',
      'displayReportedListingHint', 'displayQuickAccessMenuHint',
    ]);

    userSpy = jasmine.createSpyObj('UserService', [], {
      'sessionUser$': of(null),
      'sessionUser': null,
    });

    templatesModalSpy = jasmine.createSpyObj('TemplatesModalService', ['closeModal']);

    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    TestBed.configureTestingModule({
      providers: [
        ListingViewInteractionsService,
        { provide: BookmarkApiService, useValue: bmSpy },
        { provide: ListingReportApiService, useValue: reportApiSpy },
        { provide: HiddenListingsApiService, useValue: hideSpy },
        { provide: UiPrefsService, useValue: uiPrefSpy },
        { provide: UserService, useValue: userSpy },
        { provide: TemplatesModalService, useValue: templatesModalSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: MatDialog, useValue: dialogSpy },
      ]
    });

    service = TestBed.inject(ListingViewInteractionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('selectedListing$ starts as null', (done) => {
    service.selectedListing$.subscribe(listing => {
      expect(listing).toBeNull();
      done();
    });
  });

  it('setSelectedListing() updates selectedListing$ with the provided listing', (done) => {
    const mockListing = { groupId: 42, listingTitle: 'Test Listing' } as GroupListingViewModel;

    service.setSelectedListing(mockListing);

    service.selectedListing$.subscribe(listing => {
      expect(listing).toBe(mockListing);
      done();
    });
  });
});
