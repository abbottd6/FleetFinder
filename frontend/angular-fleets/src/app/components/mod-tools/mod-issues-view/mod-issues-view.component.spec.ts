import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';

import { ModIssuesViewComponent } from './mod-issues-view.component';
import { ModApiService } from '../../../services/api-services/mod-api/mod-api.service';
import { UiPrefsService } from '../../../services/facade-services/ui-prefs/ui-prefs.service';
import { GroupListingFetchService } from '../../../services/api-services/group-listings-fetch-api/group-listing-fetch.service';
import { ListingReportApiService } from '../../../services/api-services/listing-reports-api/listing-report-api.service';
import { ListingViewInteractionsService } from '../../../services/facade-services/listing-view-interactions/listing-view-interactions.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';

describe('ModIssuesViewComponent', () => {
  let component: ModIssuesViewComponent;
  let fixture: ComponentFixture<ModIssuesViewComponent>;

  beforeEach(async () => {
    const modApiSpy = jasmine.createSpyObj('ModApiService', ['modGetIssues']);
    modApiSpy.modGetIssues.and.returnValue(of({
      content: [],
      page: { totalElements: 0, size: 10, number: 0, totalPages: 0 }
    }));

    const uiPrefsSpy = jasmine.createSpyObj('UiPrefsService', ['loadUiPrefs'], {
      uiPrefs: { clickedRowIds: new Set(), hideHiddenListingHint: false, hideBookmarkedListingHint: false }
    });
    uiPrefsSpy.loadUiPrefs.and.returnValue({ clickedRowIds: new Set(), hideHiddenListingHint: false, hideBookmarkedListingHint: false });

    const glsSpy = jasmine.createSpyObj('GroupListingFetchService', ['getGroupById']);
    const reportsApiSpy = jasmine.createSpyObj('ListingReportApiService', ['getReportsForListing'], {
      reportOptions$: of([])
    });
    const listingInteractSpy = jasmine.createSpyObj('ListingViewInteractionsService',
      ['setSelectedListing'], { selectedListing$: of(null) });
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    const dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [ModIssuesViewComponent],
      providers: [
        { provide: ModApiService, useValue: modApiSpy },
        { provide: UiPrefsService, useValue: uiPrefsSpy },
        { provide: GroupListingFetchService, useValue: glsSpy },
        { provide: ListingReportApiService, useValue: reportsApiSpy },
        { provide: ListingViewInteractionsService, useValue: listingInteractSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(ModIssuesViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
