import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyInvitesChipComponent } from './my-invites-chip.component';

describe('MyInvitesChipComponent', () => {
  let component: MyInvitesChipComponent;
  let fixture: ComponentFixture<MyInvitesChipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MyInvitesChipComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyInvitesChipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
