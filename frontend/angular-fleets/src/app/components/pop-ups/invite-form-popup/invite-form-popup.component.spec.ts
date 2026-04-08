import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InviteFormPopupComponent } from './invite-form-popup.component';

describe('InviteFormPopupComponent', () => {
  let component: InviteFormPopupComponent;
  let fixture: ComponentFixture<InviteFormPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InviteFormPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InviteFormPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
