import { TestBed } from '@angular/core/testing';

import { GroupCompositionApiService } from './group-composition-api.service';

describe('GroupCompositionApiService', () => {
  let service: GroupCompositionApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GroupCompositionApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
