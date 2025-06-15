import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAcctListingsTableComponent } from './user-acct-listings-table.component';

describe('UserAcctListingsTableComponent', () => {
  let component: UserAcctListingsTableComponent;
  let fixture: ComponentFixture<UserAcctListingsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAcctListingsTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserAcctListingsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
