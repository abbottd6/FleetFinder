import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupsChipComponent } from './groups-chip.component';

describe('GroupsChipComponent', () => {
  let component: GroupsChipComponent;
  let fixture: ComponentFixture<GroupsChipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GroupsChipComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupsChipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
