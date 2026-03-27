import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ListingReportApiService } from './listing-report-api.service';

describe('ListingReportApiService', () => {
  let service: ListingReportApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ListingReportApiService]
    });
    service = TestBed.inject(ListingReportApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('submitReport() should POST to /api/users/group_listings/submit_report', () => {
    const mockReport: any = { groupId: 1, basisId: 2 };

    service.submitReport(mockReport).subscribe();

    const req = httpMock.expectOne(req => req.url.includes('/users/group_listings/submit_report'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockReport);
    req.flush({ success: true });
  });

  it('getListingReportBasisApi() should GET from /api/lookup/report-basis', () => {
    const mockBasis = [
      { basisId: 1, basisLabel: 'Spam' },
      { basisId: 2, basisLabel: 'Harassment' }
    ];

    service.getListingReportBasisApi().subscribe(data => {
      expect(data.length).toBe(2);
      expect(data[0].basisLabel).toBe('Spam');
    });

    const req = httpMock.expectOne(req => req.url.includes('/lookup/report-basis'));
    expect(req.request.method).toBe('GET');
    req.flush(mockBasis);
  });

  it('reportOptions$ should emit mapped options', (done) => {
    service.reportOptions$.subscribe(options => {
      expect(options.length).toBe(0);
      done();
    });
    httpMock.expectOne(req => req.url.includes('/lookup/report-basis')).flush([]);
  });
});
