import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';

import { ModActionsTableComponent } from './mod-actions-table.component';
import { ModApiService } from '../../../services/api-services/mod-api/mod-api.service';
import { UiPrefsService } from '../../../services/facade-services/ui-prefs/ui-prefs.service';

describe('ModActionsTableComponent', () => {
  let component: ModActionsTableComponent;
  let fixture: ComponentFixture<ModActionsTableComponent>;
  let modApiSpy: jasmine.SpyObj<ModApiService>;

  beforeEach(async () => {
    modApiSpy = jasmine.createSpyObj('ModApiService', ['modGetWeeksActions']);
    modApiSpy.modGetWeeksActions.and.returnValue(of({ content: [], page: { totalElements: 0, size: 10, number: 0, totalPages: 0 } } as any));

    const uiPrefsSpy = jasmine.createSpyObj('UiPrefsService', ['loadUiPrefs'], {
      uiPrefs: { clickedRowIds: new Set(), hideHiddenListingHint: false, hideBookmarkedListingHint: false }
    });
    uiPrefsSpy.loadUiPrefs.and.returnValue({ clickedRowIds: new Set(), hideHiddenListingHint: false, hideBookmarkedListingHint: false });

    await TestBed.configureTestingModule({
      imports: [ModActionsTableComponent],
      providers: [
        { provide: ModApiService, useValue: modApiSpy },
        { provide: UiPrefsService, useValue: uiPrefsSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(ModActionsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('loadWeeksActions() calls modApi.modGetWeeksActions', () => {
    component.loadWeeksActions(0, 10);
    expect(modApiSpy.modGetWeeksActions).toHaveBeenCalled();
  });
});
