import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupListingModalComponent } from './group-listing-modal.component';

describe('GroupListingModalComponent', () => {
  let component: GroupListingModalComponent;
  let fixture: ComponentFixture<GroupListingModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GroupListingModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupListingModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
