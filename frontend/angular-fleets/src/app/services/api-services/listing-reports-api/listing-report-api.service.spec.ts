import { TestBed } from '@angular/core/testing';

import { ListingReportApiService } from './listing-report-api.service';

describe('ListingReportService', () => {
  let service: ListingReportApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ListingReportApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
