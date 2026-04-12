import { TestBed } from '@angular/core/testing';

import { MemberManagementApiService } from './member-management-api.service';

describe('MemberManagementApiService', () => {
  let service: MemberManagementApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MemberManagementApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
