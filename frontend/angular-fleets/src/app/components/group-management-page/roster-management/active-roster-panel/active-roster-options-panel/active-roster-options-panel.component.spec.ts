import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActiveRosterOptionsPanelComponent } from './active-roster-options-panel.component';

describe('ActiveRosterOptionsPanelComponent', () => {
  let component: ActiveRosterOptionsPanelComponent;
  let fixture: ComponentFixture<ActiveRosterOptionsPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActiveRosterOptionsPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActiveRosterOptionsPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
