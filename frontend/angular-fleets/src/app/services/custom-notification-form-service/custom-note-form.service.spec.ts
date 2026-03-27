import { TestBed } from '@angular/core/testing';

import { CustomNoteFormService } from './custom-note-form.service';

describe('CustomNoteFormService', () => {
  let service: CustomNoteFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CustomNoteFormService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
