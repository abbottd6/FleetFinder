import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemberChipComponent } from './member-chip.component';

describe('MemberChipComponent', () => {
  let component: MemberChipComponent;
  let fixture: ComponentFixture<MemberChipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MemberChipComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MemberChipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
