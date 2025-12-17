import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserBookmarksTableComponent } from './user-bookmarks-table.component';

describe('UserBookmarksTableComponent', () => {
  let component: UserBookmarksTableComponent;
  let fixture: ComponentFixture<UserBookmarksTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserBookmarksTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserBookmarksTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
