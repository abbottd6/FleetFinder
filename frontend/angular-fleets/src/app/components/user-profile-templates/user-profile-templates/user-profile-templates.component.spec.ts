import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserProfileTemplatesComponent } from './user-profile-templates.component';

describe('UserProfileTemplatesComponent', () => {
  let component: UserProfileTemplatesComponent;
  let fixture: ComponentFixture<UserProfileTemplatesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserProfileTemplatesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserProfileTemplatesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
