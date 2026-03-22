import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PushNotificationFormComponent } from './push-notification-form.component';

describe('PushNotificationFormComponent', () => {
  let component: PushNotificationFormComponent;
  let fixture: ComponentFixture<PushNotificationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PushNotificationFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PushNotificationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
