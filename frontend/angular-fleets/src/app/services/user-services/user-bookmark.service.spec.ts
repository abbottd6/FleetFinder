import { TestBed } from '@angular/core/testing';

import { UserBookmarkService } from './user-bookmark.service';

describe('UserBookmarkService', () => {
  let service: UserBookmarkService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserBookmarkService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
