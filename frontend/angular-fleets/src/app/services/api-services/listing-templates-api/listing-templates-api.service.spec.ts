import { TestBed } from '@angular/core/testing';

import { ListingTemplatesApiService } from './listing-templates-api.service';

describe('ListingTemplatesApiService', () => {
  let service: ListingTemplatesApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ListingTemplatesApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
