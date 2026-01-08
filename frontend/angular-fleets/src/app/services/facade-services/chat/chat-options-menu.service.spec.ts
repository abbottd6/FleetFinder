import { TestBed } from '@angular/core/testing';

import { ChatOptionsMenuService } from './chat-options-menu.service';

describe('ChatOptionsMenuService', () => {
  let service: ChatOptionsMenuService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChatOptionsMenuService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
