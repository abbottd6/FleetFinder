import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RosterTextFieldFilterComponent } from './roster-text-field-filter.component';

describe('RosterTextFieldFilterComponent', () => {
  let component: RosterTextFieldFilterComponent;
  let fixture: ComponentFixture<RosterTextFieldFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RosterTextFieldFilterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RosterTextFieldFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
