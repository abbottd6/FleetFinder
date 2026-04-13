import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmInviteActionPopupComponent } from './confirm-invite-action-popup.component';

describe('ConfirmInviteActionPopupComponent', () => {
  let component: ConfirmInviteActionPopupComponent;
  let fixture: ComponentFixture<ConfirmInviteActionPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmInviteActionPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfirmInviteActionPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
