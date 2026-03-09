import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { ListingFormService } from './listing-form.service';
import { LookupService } from '../api-services/reference-data-api/lookup.service';

describe('ListingFormService', () => {
  let service: ListingFormService;

  beforeEach(() => {
    const lookupSpy = jasmine.createSpyObj('LookupService', [
      'getServerRegions', 'getGameEnvironments', 'getGameExperiences', 'getPlayStyles',
      'getLegalities', 'getGroupStatuses', 'getGameplayCategories', 'getGameplaySubcategories',
      'getPvpStatuses', 'getPlanetarySystems', 'getPlanetMoonSystems'
    ]);

    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        ListingFormService,
        { provide: LookupService, useValue: lookupSpy }
      ]
    });
    service = TestBed.inject(ListingFormService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should build a form group with all expected controls', () => {
    expect(service.listingTitle).toBeTruthy();
    expect(service.serverRegion).toBeTruthy();
    expect(service.gameEnvironment).toBeTruthy();
    expect(service.gameExperience).toBeTruthy();
    expect(service.playStyle).toBeTruthy();
    expect(service.category).toBeTruthy();
    expect(service.subcategoryControl).toBeTruthy();
    expect(service.legality).toBeTruthy();
    expect(service.pvpStatus).toBeTruthy();
    expect(service.planetarySystem).toBeTruthy();
    expect(service.planetMoon).toBeTruthy();
    expect(service.groupStatus).toBeTruthy();
    expect(service.language).toBeTruthy();
  });

  it('nullable reference data controls should have null initial values', () => {
    expect(service.serverRegion.value).toBeNull();
    expect(service.category.value).toBeNull();
    expect(service.groupStatus.value).toBeNull();
    expect(service.language.value).toBeNull();
    expect(service.legality.value).toBeNull();
    expect(service.pvpStatus.value).toBeNull();
    expect(service.planetarySystem.value).toBeNull();
    expect(service.playStyle.value).toBeNull();
  });

  it('language control should accept a valid LanguageCode value', () => {
    service.language.setValue('EN');
    expect(service.language.value).toBe('EN');
    expect(service.language.valid).toBeTrue();
  });

  it('serverRegion control should accept a number', () => {
    service.serverRegion.setValue(1);
    expect(service.serverRegion.value).toBe(1);
  });

  it('subcategoryControl and planetMoon should start disabled', () => {
    expect(service.subcategoryControl.disabled).toBeTrue();
    expect(service.planetMoon.disabled).toBeTrue();
  });
});
