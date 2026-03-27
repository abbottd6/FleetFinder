import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { UiCleanupService } from './ui-cleanup.service';

describe('UiCleanupService', () => {
  let service: UiCleanupService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UiCleanupService]
    });
    service = TestBed.inject(UiCleanupService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('cleanClickedListings() should PUT to /api/be-busy/clicked-clean with the id array', () => {
    const ids = [1, 2, 3];

    service.cleanClickedListings(ids).subscribe();

    const req = httpMock.expectOne(req => req.url.includes('/be-busy/clicked-clean'));
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(ids);
    req.flush(null);
  });

  it('cleanClickedListings() should PUT with an empty array when no ids are provided', () => {
    service.cleanClickedListings([]).subscribe();

    const req = httpMock.expectOne(req => req.url.includes('/be-busy/clicked-clean'));
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual([]);
    req.flush(null);
  });
});
