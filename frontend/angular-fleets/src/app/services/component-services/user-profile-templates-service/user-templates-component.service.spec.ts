import { TestBed } from '@angular/core/testing';

import { UserTemplatesComponentService } from './user-templates-component.service';

describe('UserTemplatesComponentService', () => {
  let service: UserTemplatesComponentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserTemplatesComponentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
