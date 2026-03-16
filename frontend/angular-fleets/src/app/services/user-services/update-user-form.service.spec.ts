import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { UpdateUserFormService } from './update-user-form.service';
import { UserService } from './user.service';
import { LookupService } from '../api-services/reference-data-api/lookup.service';

describe('UpdateUserFormServiceService', () => {
  let service: UpdateUserFormService;

  beforeEach(() => {
    const userServiceSpy = jasmine.createSpyObj('UserService', [], {
      server: null,
      org: null
    });
    const lookupSpy = jasmine.createSpyObj('LookupService', ['getServerRegions']);
    lookupSpy.getServerRegions.and.returnValue(of([]));

    TestBed.configureTestingModule({
      providers: [
        UpdateUserFormService,
        { provide: UserService, useValue: userServiceSpy },
        { provide: LookupService, useValue: lookupSpy }
      ]
    });
    service = TestBed.inject(UpdateUserFormService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
