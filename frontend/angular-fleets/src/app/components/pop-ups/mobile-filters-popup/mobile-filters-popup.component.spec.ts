import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MobileFiltersPopupComponent } from './mobile-filters-popup.component';

describe('ListingViewMobileFiltersPopupComponent', () => {
  let component: MobileFiltersPopupComponent;
  let fixture: ComponentFixture<MobileFiltersPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MobileFiltersPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MobileFiltersPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
