import { TestBed } from '@angular/core/testing';

import { PublicSocialApiService } from './public-social-api.service';

describe('PublicSocialApiService', () => {
  let service: PublicSocialApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PublicSocialApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
