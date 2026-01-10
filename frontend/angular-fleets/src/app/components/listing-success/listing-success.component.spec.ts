import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListingSuccessComponent } from './listing-success.component';

describe('ListingSuccessComponent', () => {
  let component: ListingSuccessComponent;
  let fixture: ComponentFixture<ListingSuccessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListingSuccessComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListingSuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
