import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserProfileBookmarksComponent } from './user-profile-bookmarks.component';

describe('UserBookmarksTableComponent', () => {
  let component: UserProfileBookmarksComponent;
  let fixture: ComponentFixture<UserProfileBookmarksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserProfileBookmarksComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserProfileBookmarksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
