import { TestBed } from '@angular/core/testing';

import { ChatOptionsMenuService } from './chat-options-menu.service';

describe('ChatOptionsMenuService', () => {
  let service: ChatOptionsMenuService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ChatOptionsMenuService]
    });
    service = TestBed.inject(ChatOptionsMenuService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('longPressTriggered should start as false', () => {
    expect(service.longPressTriggered).toBeFalse();
  });

  it('onTouchEnd() should clear the long press timer and set longPressTriggered to false', () => {
    service.longPressTriggered = true;
    const mockEvent = { preventDefault: jasmine.createSpy('preventDefault'), touches: [] } as unknown as TouchEvent;

    service.onTouchEnd(mockEvent);

    expect(service.longPressTriggered).toBeFalse();
    expect(mockEvent.preventDefault).toHaveBeenCalled();
  });

  it('onTouchStart() should reset longPressTriggered to false at the start of a touch', () => {
    service.longPressTriggered = true;
    const mockTouch = { clientX: 100, clientY: 200 } as Touch;
    const mockEvent = {
      touches: [mockTouch],
      preventDefault: jasmine.createSpy('preventDefault')
    } as unknown as TouchEvent;

    service.onTouchStart(mockEvent);

    expect(service.longPressTriggered).toBeFalse();
    clearTimeout(service.longPressTimer);
  });
});
