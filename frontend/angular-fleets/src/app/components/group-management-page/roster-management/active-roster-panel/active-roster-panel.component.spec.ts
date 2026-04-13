import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActiveRosterPanelComponent } from './active-roster-panel.component';

describe('ActiveRosterPanelComponent', () => {
  let component: ActiveRosterPanelComponent;
  let fixture: ComponentFixture<ActiveRosterPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActiveRosterPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActiveRosterPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
