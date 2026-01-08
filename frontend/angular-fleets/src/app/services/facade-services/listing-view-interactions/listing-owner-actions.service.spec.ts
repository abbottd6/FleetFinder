import { TestBed } from '@angular/core/testing';

import { ListingOwnerActionsService } from './listing-owner-actions.service';

describe('ListingOwnerActionsService', () => {
  let service: ListingOwnerActionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ListingOwnerActionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
