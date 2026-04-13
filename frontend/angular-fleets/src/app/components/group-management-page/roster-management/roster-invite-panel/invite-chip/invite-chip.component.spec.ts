import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InviteChipComponent } from './invite-chip.component';

describe('InviteChipComponent', () => {
  let component: InviteChipComponent;
  let fixture: ComponentFixture<InviteChipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InviteChipComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InviteChipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
