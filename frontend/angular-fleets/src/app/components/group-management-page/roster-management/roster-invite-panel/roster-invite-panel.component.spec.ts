import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RosterInvitePanelComponent } from './roster-invite-panel.component';

describe('RosterInvitePanelComponent', () => {
  let component: RosterInvitePanelComponent;
  let fixture: ComponentFixture<RosterInvitePanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RosterInvitePanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RosterInvitePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
