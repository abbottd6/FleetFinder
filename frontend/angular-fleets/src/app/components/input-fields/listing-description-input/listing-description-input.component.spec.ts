import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListingDescriptionInputComponent } from './listing-description-input.component';

describe('ListingDescriptionInputComponent', () => {
  let component: ListingDescriptionInputComponent;
  let fixture: ComponentFixture<ListingDescriptionInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListingDescriptionInputComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListingDescriptionInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
