import { TestBed } from '@angular/core/testing';

import { GroupCompositionInteractService } from './group-composition-interact.service';

describe('SubgroupManagementInteractService', () => {
  let service: GroupCompositionInteractService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GroupCompositionInteractService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
