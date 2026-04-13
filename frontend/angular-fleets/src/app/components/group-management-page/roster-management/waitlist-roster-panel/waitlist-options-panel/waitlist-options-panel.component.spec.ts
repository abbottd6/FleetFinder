import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WaitlistOptionsPanelComponent } from './waitlist-options-panel.component';

describe('WaitlistOptionsPanelComponent', () => {
  let component: WaitlistOptionsPanelComponent;
  let fixture: ComponentFixture<WaitlistOptionsPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WaitlistOptionsPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WaitlistOptionsPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
