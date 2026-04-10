import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserProfileMyGroupsComponent } from './user-profile-my-groups.component';

describe('UserProfileMyGroupsComponent', () => {
  let component: UserProfileMyGroupsComponent;
  let fixture: ComponentFixture<UserProfileMyGroupsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserProfileMyGroupsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserProfileMyGroupsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
