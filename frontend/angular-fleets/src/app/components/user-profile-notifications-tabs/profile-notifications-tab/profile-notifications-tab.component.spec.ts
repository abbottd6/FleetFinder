import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileNotificationsTabComponent } from './profile-notifications-tab.component';

describe('ProfileNotificationsTabComponent', () => {
  let component: ProfileNotificationsTabComponent;
  let fixture: ComponentFixture<ProfileNotificationsTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProfileNotificationsTabComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfileNotificationsTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
