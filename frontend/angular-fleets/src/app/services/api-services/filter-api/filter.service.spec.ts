import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { FilterService, ListingFilterState } from './filter.service';
import { LookupService } from '../reference-data-api/lookup.service';

const NULL_FILTER_STATE: ListingFilterState = {
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
};

describe('FilterService', () => {
  let service: FilterService;
  let lookupSpy: jasmine.SpyObj<LookupService>;

  beforeEach(() => {
    lookupSpy = jasmine.createSpyObj('LookupService', [
      'getGroupStatuses', 'getServerRegions', 'getGameEnvironments',
      'getGameExperiences', 'getGameplayCategories', 'getGameplaySubcategories',
      'getPlanetarySystems', 'getPlanetMoonSystems', 'getPvpStatuses',
      'getLegalities', 'getPlayStyles',
    ]);

    // Provide safe defaults so methods don't throw if called during construction
    lookupSpy.getGroupStatuses.and.returnValue(of([]));
    lookupSpy.getServerRegions.and.returnValue(of([]));

    TestBed.configureTestingModule({
      providers: [
        FilterService,
        { provide: LookupService, useValue: lookupSpy },
      ]
    });

    service = TestBed.inject(FilterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('state$ emits the initial all-null FilterState', (done) => {
    service.state$.subscribe(state => {
      expect(state).toEqual(NULL_FILTER_STATE);
      done();
    });
  });

  it('clearFilters() resets state to all-null defaults', (done) => {
    service.update('searchInput', 'query');
    service.clearFilters();

    service.state$.subscribe(state => {
      expect(state).toEqual(NULL_FILTER_STATE);
      done();
    });
  });

  it('update() merges a single key into state', (done) => {
    service.update('searchInput', 'test-query');

    service.state$.subscribe(state => {
      expect(state.searchInput).toBe('test-query');
      expect(state.server).toBeNull();
      done();
    });
  });
});
