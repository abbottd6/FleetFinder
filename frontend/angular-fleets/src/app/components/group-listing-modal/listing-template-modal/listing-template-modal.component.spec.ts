import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListingTemplateModalComponent } from './listing-template-modal.component';

describe('ListingTemplateModalComponent', () => {
  let component: ListingTemplateModalComponent;
  let fixture: ComponentFixture<ListingTemplateModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListingTemplateModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListingTemplateModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
