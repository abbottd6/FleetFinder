import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AcceptInviteOfferPopupFormComponent } from './accept-invite-offer-popup-form.component';

describe('AcceptInviteOfferPopupFormComponent', () => {
  let component: AcceptInviteOfferPopupFormComponent;
  let fixture: ComponentFixture<AcceptInviteOfferPopupFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AcceptInviteOfferPopupFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AcceptInviteOfferPopupFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
