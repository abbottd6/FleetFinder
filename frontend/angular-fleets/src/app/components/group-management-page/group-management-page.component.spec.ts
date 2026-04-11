import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupManagementPageComponent } from './group-management-page.component';

describe('GroupManagementPageComponent', () => {
  let component: GroupManagementPageComponent;
  let fixture: ComponentFixture<GroupManagementPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GroupManagementPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupManagementPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
