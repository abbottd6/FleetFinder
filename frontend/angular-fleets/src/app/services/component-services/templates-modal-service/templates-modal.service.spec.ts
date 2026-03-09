import { TestBed } from '@angular/core/testing';

import { TemplatesModalService } from './templates-modal.service';
import { CloseValue } from '../../../components/group-listing-modal/group-listing-modal.component';

describe('TemplatesModalService', () => {
  let service: TemplatesModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TemplatesModalService]
    });
    service = TestBed.inject(TemplatesModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('templateModalIsVisible should start as false', () => {
    expect(service.templateModalIsVisible).toBeFalse();
  });

  it('closeModal() should set templateModalIsVisible to false and emit on close EventEmitter', () => {
    service.templateModalIsVisible = true;

    let emitted: CloseValue | undefined;
    service.close.subscribe((val: CloseValue) => {
      emitted = val;
    });

    service.closeModal(null);

    expect(service.templateModalIsVisible).toBeFalse();
    expect(emitted).toBeDefined();
    expect(emitted!.value).toBeNull();
    expect(emitted!.group).toBeNull();
  });

  it('closeModal() with "delete" action should emit on refresh$ and on close EventEmitter', () => {
    let refreshValue: string | null | undefined;
    service.refresh$.subscribe(val => {
      refreshValue = val;
    });

    let closeEmitted: CloseValue | undefined;
    service.close.subscribe((val: CloseValue) => {
      closeEmitted = val;
    });

    service.closeModal('delete');

    expect(refreshValue).toBe('delete');
    expect(closeEmitted!.value).toBe('delete');
  });
});
