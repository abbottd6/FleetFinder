import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { QuickAccessMenuService } from './quick-access-menu.service';
import { ListingViewInteractionsService } from '../../facade-services/listing-view-interactions/listing-view-interactions.service';

describe('QuickAccessMenuService', () => {
  let service: QuickAccessMenuService;
  let listingInteractSpy: jasmine.SpyObj<ListingViewInteractionsService>;

  beforeEach(() => {
    listingInteractSpy = jasmine.createSpyObj('ListingViewInteractionsService', ['setSelectedListing'], {
      'selectedListing$': of(null),
    });

    TestBed.configureTestingModule({
      providers: [
        QuickAccessMenuService,
        { provide: ListingViewInteractionsService, useValue: listingInteractSpy },
      ]
    });

    service = TestBed.inject(QuickAccessMenuService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('longPressTriggered starts as false', () => {
    expect(service.longPressTriggered).toBeFalse();
  });

  it('onTouchEnd() resets longPressTriggered to false', () => {
    service.longPressTriggered = true;
    const event = new TouchEvent('touchend', { touches: [], cancelable: true });
    service.onTouchEnd(event);
    expect(service.longPressTriggered).toBeFalse();
  });
});
