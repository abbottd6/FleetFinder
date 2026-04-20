import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthorizedMemberGroupSelectDropdownComponent } from './authorized-member-group-select-dropdown.component';

describe('AuthorizedMemberGroupSelectDropdownComponent', () => {
  let component: AuthorizedMemberGroupSelectDropdownComponent;
  let fixture: ComponentFixture<AuthorizedMemberGroupSelectDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AuthorizedMemberGroupSelectDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AuthorizedMemberGroupSelectDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
