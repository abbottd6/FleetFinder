import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { LookupService } from './lookup.service';
import {
  GameplaySubcategory,
  PlanetMoonSystem,
  ServerRegion
} from '../../../models/reference-data/reference-data.models';

describe('LookupService', () => {
  let service: LookupService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(LookupService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getServerRegions() should call the correct endpoint and return ServerRegion[]', () => {
    const mockData: ServerRegion[] = [
      { serverId: 1, servername: 'USA' },
      { serverId: 2, servername: 'EU' }
    ];

    service.getServerRegions().subscribe(data => {
      expect(data.length).toBe(2);
      expect(data[0].serverId).toBe(1);
      expect(data[0].servername).toBe('USA');
    });

    const req = httpMock.expectOne(req => req.url.includes('/lookup/server-regions'));
    expect(req.request.method).toBe('GET');
    req.flush(mockData);
  });

  it('getServerRegions() should cache the result and not make a second HTTP request', () => {
    const mockData: ServerRegion[] = [{ serverId: 1, servername: 'USA' }];

    service.getServerRegions().subscribe();
    httpMock.expectOne(req => req.url.includes('/lookup/server-regions')).flush(mockData);

    service.getServerRegions().subscribe(data => {
      expect(data[0].servername).toBe('USA');
    });
    httpMock.expectNone(req => req.url.includes('/lookup/server-regions'));
  });

  it('getGameplaySubcategories() should return objects with the correct shape', () => {
    const mockData: GameplaySubcategory[] = [{
      subcategoryId: 10,
      subcategoryName: 'Prospecting',
      gameplayCategoryId: 3,
      gameplayCategoryName: 'Mining'
    }];

    service.getGameplaySubcategories().subscribe(data => {
      expect(data[0].subcategoryId).toBe(10);
      expect(data[0].gameplayCategoryId).toBe(3);
    });

    httpMock.expectOne(req => req.url.includes('/lookup/gameplay-subcategories')).flush(mockData);
  });

  it('getPlanetMoonSystems() should return objects with planetId, planetName, systemId, systemName', () => {
    const mockData: PlanetMoonSystem[] = [{
      planetId: 5, planetName: 'Hurston', systemId: 1, systemName: 'Stanton'
    }];

    service.getPlanetMoonSystems().subscribe(data => {
      expect(data[0].planetId).toBe(5);
      expect(data[0].systemId).toBe(1);
    });

    httpMock.expectOne(req => req.url.includes('/lookup/planet-moon-systems')).flush(mockData);
  });

  const endpointCases: [string, string][] = [
    ['getGameEnvironments',   '/lookup/game-environments'],
    ['getGameExperiences',    '/lookup/game-experiences'],
    ['getPlayStyles',         '/lookup/play-styles'],
    ['getLegalities',         '/lookup/legalities'],
    ['getGroupStatuses',      '/lookup/group-statuses'],
    ['getGameplayCategories', '/lookup/gameplay-categories'],
    ['getPvpStatuses',        '/lookup/pvp-statuses'],
    ['getPlanetarySystems',   '/lookup/planetary-systems'],
  ];

  endpointCases.forEach(([method, path]) => {
    it(`${method}() should call ${path}`, () => {
      (service as any)[method]().subscribe();
      const req = httpMock.expectOne(req => req.url.includes(path));
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });
  });
});
