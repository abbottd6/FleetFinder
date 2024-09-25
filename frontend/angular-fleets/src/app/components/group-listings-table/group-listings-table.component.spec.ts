import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupListingsTableComponent } from './group-listings-table.component';

describe('GroupListingsComponent', () => {
  let component: GroupListingsTableComponent;
  let fixture: ComponentFixture<GroupListingsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GroupListingsTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupListingsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
