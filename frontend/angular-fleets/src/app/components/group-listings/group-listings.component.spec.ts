import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupListingsComponent } from './group-listings.component';

describe('GroupListingsComponent', () => {
  let component: GroupListingsComponent;
  let fixture: ComponentFixture<GroupListingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GroupListingsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupListingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
