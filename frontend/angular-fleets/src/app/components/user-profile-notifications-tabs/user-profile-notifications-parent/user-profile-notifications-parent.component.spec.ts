import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserProfileNotificationsParentComponent } from './user-profile-notifications-parent.component';

describe('UserProfileNotificationsComponent', () => {
  let component: UserProfileNotificationsParentComponent;
  let fixture: ComponentFixture<UserProfileNotificationsParentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserProfileNotificationsParentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserProfileNotificationsParentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
