import { TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';

import { UiPrefsService } from './ui-prefs.service';
import { UiCleanupService } from '../../cleanup-services/ui-cleanup.service';
import { FilterService } from '../../api-services/filter-api/filter.service';

describe('UiPrefsService', () => {
  let service: UiPrefsService;
  let uiCleanupSpy: jasmine.SpyObj<UiCleanupService>;
  let filterSpy: jasmine.SpyObj<FilterService>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  beforeEach(() => {
    uiCleanupSpy = jasmine.createSpyObj('UiCleanupService', ['cleanClickedListings']);

    filterSpy = jasmine.createSpyObj('FilterService', ['pullState', 'clearFilters', 'update']);
    filterSpy.pullState.and.returnValue({
      searchInput: null,
      server: null,
      environment: null,
      experience: null,
      playStyle: null,
      category: null,
      subcategory: null,
      legality: null,
      pvpStatus: null,
      system: null,
      planetMoonSystem: null,
      groupStatus: null,
      dateStart: null,
      dateEnd: null,
      commsOption: null,
      language: null,
    });

    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    TestBed.configureTestingModule({
      providers: [
        UiPrefsService,
        { provide: UiCleanupService, useValue: uiCleanupSpy },
        { provide: FilterService, useValue: filterSpy },
        { provide: MatDialog, useValue: dialogSpy },
      ]
    });

    service = TestBed.inject(UiPrefsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('loadUiPrefs() returns a UiPrefs object with required fields', () => {
    const prefs = service.loadUiPrefs();

    expect(prefs).toBeDefined();
    expect(prefs.clickedRowIds).toBeInstanceOf(Set);
    expect(typeof prefs.hideHiddenListingHint).toBe('boolean');
    expect(typeof prefs.hideBookmarkedListingHint).toBe('boolean');
  });

  it('loadUiPrefs() does not throw when localStorage has no stored prefs', () => {
    localStorage.removeItem('ff_ui_prefs');
    expect(() => service.loadUiPrefs()).not.toThrow();
  });
});
