import { TestBed } from '@angular/core/testing';

import { ListingReportService } from './listing-report.service';

describe('ListingReportService', () => {
  let service: ListingReportService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ListingReportService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
