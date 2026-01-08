import { TestBed } from '@angular/core/testing';

import { ListingViewInteractionsService } from './listing-view-interactions.service';

describe('ListingViewActionsService', () => {
  let service: ListingViewInteractionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ListingViewInteractionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
