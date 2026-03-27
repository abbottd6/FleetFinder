import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomNotificationFormComponent } from './custom-notification-form.component';

describe('CustomNotificationFormComponent', () => {
  let component: CustomNotificationFormComponent;
  let fixture: ComponentFixture<CustomNotificationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CustomNotificationFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomNotificationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
