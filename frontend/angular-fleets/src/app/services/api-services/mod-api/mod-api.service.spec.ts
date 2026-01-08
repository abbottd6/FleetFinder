import { TestBed } from '@angular/core/testing';

import { ModServiceService } from './mod-api.service';

describe('ModServiceService', () => {
  let service: ModServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
