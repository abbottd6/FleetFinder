import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListingDiscoveryTypeComponent } from './listing-discovery-type.component';

describe('ListingDiscoveryTypeComponent', () => {
  let component: ListingDiscoveryTypeComponent;
  let fixture: ComponentFixture<ListingDiscoveryTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListingDiscoveryTypeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListingDiscoveryTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
