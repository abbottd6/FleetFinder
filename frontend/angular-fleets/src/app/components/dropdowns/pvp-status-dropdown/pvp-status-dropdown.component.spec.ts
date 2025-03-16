import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PvpStatusDropdownComponent } from './pvp-status-dropdown.component';

describe('PvpStatusDropdownComponent', () => {
  let component: PvpStatusDropdownComponent;
  let fixture: ComponentFixture<PvpStatusDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PvpStatusDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PvpStatusDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
