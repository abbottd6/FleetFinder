import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserDeleteAccountPopupComponent } from './user-delete-account-popup.component';

describe('UserDeleteAccountPopupComponent', () => {
  let component: UserDeleteAccountPopupComponent;
  let fixture: ComponentFixture<UserDeleteAccountPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserDeleteAccountPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserDeleteAccountPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
