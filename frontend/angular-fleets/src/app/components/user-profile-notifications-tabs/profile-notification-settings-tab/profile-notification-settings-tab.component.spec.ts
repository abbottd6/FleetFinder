import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileNotificationSettingsTabComponent } from './profile-notification-settings-tab.component';

describe('ProfileNotificationSettingsTabComponent', () => {
  let component: ProfileNotificationSettingsTabComponent;
  let fixture: ComponentFixture<ProfileNotificationSettingsTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProfileNotificationSettingsTabComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfileNotificationSettingsTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
