import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationChipGenericComponent } from './notification-chip-generic.component';

describe('NotificationChipGenericComponent', () => {
  let component: NotificationChipGenericComponent;
  let fixture: ComponentFixture<NotificationChipGenericComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotificationChipGenericComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotificationChipGenericComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
