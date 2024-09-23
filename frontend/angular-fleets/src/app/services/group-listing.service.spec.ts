import { TestBed } from '@angular/core/testing';

import { GroupListingService } from './group-listing.service';

describe('GroupListingService', () => {
  let service: GroupListingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GroupListingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
