import { TestBed } from '@angular/core/testing';

import { GroupManagementInteractService } from './group-management-interact.service';

describe('GroupManagementInteractService', () => {
  let service: GroupManagementInteractService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GroupManagementInteractService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
