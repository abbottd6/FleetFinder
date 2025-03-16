import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LegalityDropdownComponent } from './legality-dropdown.component';

describe('LegalityDropdownComponent', () => {
  let component: LegalityDropdownComponent;
  let fixture: ComponentFixture<LegalityDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LegalityDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LegalityDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
