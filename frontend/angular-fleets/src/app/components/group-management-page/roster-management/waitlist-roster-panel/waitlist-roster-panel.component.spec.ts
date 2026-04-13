import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WaitlistRosterPanelComponent } from './waitlist-roster-panel.component';

describe('WaitlistRosterPanelComponent', () => {
  let component: WaitlistRosterPanelComponent;
  let fixture: ComponentFixture<WaitlistRosterPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WaitlistRosterPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WaitlistRosterPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
