import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { ReactiveFormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';
import { FilterDropdownsComponent } from './filter-dropdowns.component';
import { FilterService } from '../../../services/api-services/filter-api/filter.service';

describe('FilterDropdownsComponent', () => {
  let component: FilterDropdownsComponent;
  let fixture: ComponentFixture<FilterDropdownsComponent>;
  let filterServiceSpy: jasmine.SpyObj<FilterService>;

  beforeEach(async () => {
    filterServiceSpy = jasmine.createSpyObj('FilterService', [
      'filterGroupStatus',
      'filterServerRegions',
      'filterEnvironments',
      'filterExperiences',
      'filterCategories',
      'filterSubcategories',
      'filterSystems',
      'filterPlanets',
      'filterPvp',
      'filterLegalities',
      'filterPlayStyles',
      'updateOption',
      'update',
      'pullState',
      'clearFilters',
    ]);

    const emptyOptions = of([]);
    filterServiceSpy.filterGroupStatus.and.returnValue(emptyOptions);
    filterServiceSpy.filterServerRegions.and.returnValue(emptyOptions);
    filterServiceSpy.filterEnvironments.and.returnValue(emptyOptions);
    filterServiceSpy.filterExperiences.and.returnValue(emptyOptions);
    filterServiceSpy.filterCategories.and.returnValue(emptyOptions);
    filterServiceSpy.filterSubcategories.and.returnValue(emptyOptions);
    filterServiceSpy.filterSystems.and.returnValue(emptyOptions);
    filterServiceSpy.filterPlanets.and.returnValue(emptyOptions);
    filterServiceSpy.filterPvp.and.returnValue(emptyOptions);
    filterServiceSpy.filterLegalities.and.returnValue(emptyOptions);
    filterServiceSpy.filterPlayStyles.and.returnValue(emptyOptions);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, NgSelectModule],
      declarations: [FilterDropdownsComponent],
      providers: [{ provide: FilterService, useValue: filterServiceSpy }],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FilterDropdownsComponent);
    component = fixture.componentInstance;
    component.mobileApplyButtonCheck = false;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialise with no principal filter selected', () => {
    expect(component.principalCtrl.value).toBeNull();
  });

  it('should have the expected number of filter categories defined', () => {
    expect(component.FILTER_CATEGORIES.length).toBeGreaterThan(0);
  });

  it('should have comms options defined', () => {
    expect(component.COMMS_OPTIONS.length).toBe(3);
  });
});
