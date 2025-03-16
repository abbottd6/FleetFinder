import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListingTitleInputComponent } from './listing-title-input.component';

describe('ListingTitleInputComponent', () => {
  let component: ListingTitleInputComponent;
  let fixture: ComponentFixture<ListingTitleInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListingTitleInputComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListingTitleInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
