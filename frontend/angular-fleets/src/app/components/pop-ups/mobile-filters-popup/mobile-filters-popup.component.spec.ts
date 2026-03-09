import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { of } from 'rxjs';
import { MobileFiltersPopupComponent } from './mobile-filters-popup.component';
import { FilterService } from '../../../services/api-services/filter-api/filter.service';

describe('ListingViewMobileFiltersPopupComponent', () => {
  let component: MobileFiltersPopupComponent;
  let fixture: ComponentFixture<MobileFiltersPopupComponent>;

  beforeEach(async () => {
    const dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);
    const filterSpy = jasmine.createSpyObj('FilterService', ['pushStoredState', 'pullState', 'update', 'clearFilters'], {
      state$: of([])
    });

    await TestBed.configureTestingModule({
      declarations: [MobileFiltersPopupComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: { currentFilters: of([]) } },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: FilterService, useValue: filterSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(MobileFiltersPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
