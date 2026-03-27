import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { GroupListingFetchService } from './group-listing-fetch.service';
import { ListingFilterRequest } from '../../../models/listing-filter/listing-filter-request';

describe('GroupListingFetchService', () => {
  let service: GroupListingFetchService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [GroupListingFetchService]
    });
    service = TestBed.inject(GroupListingFetchService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('searchGroupListings() should POST to /api/group-listings/search with filters and page params', () => {
    const filters: ListingFilterRequest = {} as ListingFilterRequest;
    const mockResponse: any = {
      content: [{ idGroup: 1, title: 'Test Group' }],
      page: { size: 10, number: 0, totalElements: 1, totalPages: 1 }
    };

    service.searchGroupListings(filters, 0, 10, 'createdAt', 'DESC').subscribe((response: any) => {
      expect(response.content.length).toBe(1);
      expect(response.content[0].idGroup).toBe(1);
    });

    const req = httpMock.expectOne(req => req.url.includes('/group-listings/search'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body.page).toBe(0);
    expect(req.request.body.size).toBe(10);
    expect(req.request.body.sortField).toBe('createdAt');
    expect(req.request.body.sortDirection).toBe('DESC');
    req.flush(mockResponse);
  });

  it('getGroupById() should send GET to /api/group-listings/{id}', () => {
    const mockListing: any = { idGroup: 42, title: 'Alpha Fleet' };

    service.getGroupById(42).subscribe((listing: any) => {
      expect(listing.idGroup).toBe(42);
      expect(listing.title).toBe('Alpha Fleet');
    });

    const req = httpMock.expectOne(req => req.url.includes('/group-listings/42'));
    expect(req.request.method).toBe('GET');
    req.flush(mockListing);
  });
});
