import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupStatusDropdownComponent } from './group-status-dropdown.component';

describe('GroupStatusDropdownComponent', () => {
  let component: GroupStatusDropdownComponent;
  let fixture: ComponentFixture<GroupStatusDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GroupStatusDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupStatusDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
