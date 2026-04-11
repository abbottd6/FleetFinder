import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RosterManagementComponent } from './roster-management.component';

describe('RosterManagementComponent', () => {
  let component: RosterManagementComponent;
  let fixture: ComponentFixture<RosterManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RosterManagementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RosterManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
