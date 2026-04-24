import { TestBed } from '@angular/core/testing';

import { GroupManagementUiPrefsService } from './group-management-ui-prefs.service';

describe('GroupManagementUiPrefsService', () => {
  let service: GroupManagementUiPrefsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GroupManagementUiPrefsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
