import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomNotificationChipComponent } from './custom-notification-chip.component';

describe('CustomNotificationChipComponent', () => {
  let component: CustomNotificationChipComponent;
  let fixture: ComponentFixture<CustomNotificationChipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CustomNotificationChipComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomNotificationChipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
