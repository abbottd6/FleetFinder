import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InviteOptionsPanelComponent } from './invite-options-panel.component';

describe('InviteOptionsPanelComponent', () => {
  let component: InviteOptionsPanelComponent;
  let fixture: ComponentFixture<InviteOptionsPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InviteOptionsPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InviteOptionsPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
