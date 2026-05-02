import { TestBed } from '@angular/core/testing';

import { SubgroupManagementInteractService } from './subgroup-management-interact.service';

describe('SubgroupManagementInteractService', () => {
  let service: SubgroupManagementInteractService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubgroupManagementInteractService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
