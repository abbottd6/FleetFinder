import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SendGroupInvitePopupComponent } from './send-group-invite-popup.component';

describe('SendGroupInvitePopupComponent', () => {
  let component: SendGroupInvitePopupComponent;
  let fixture: ComponentFixture<SendGroupInvitePopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SendGroupInvitePopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SendGroupInvitePopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
