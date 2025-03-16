import { TestBed } from '@angular/core/testing';

import { GroupListingFetchService } from './group-listing-fetch.service';

describe('GroupListingService', () => {
  let service: GroupListingFetchService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GroupListingFetchService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
