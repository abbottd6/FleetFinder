import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PushSubscriptionChipComponent } from './push-subscription-chip.component';

describe('PushSubscriptionChipComponent', () => {
  let component: PushSubscriptionChipComponent;
  let fixture: ComponentFixture<PushSubscriptionChipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PushSubscriptionChipComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PushSubscriptionChipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
