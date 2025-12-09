import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilterDropdownsComponent } from './filter-dropdowns.component';

describe('FilterDropdownsComponent', () => {
  let component: FilterDropdownsComponent;
  let fixture: ComponentFixture<FilterDropdownsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FilterDropdownsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FilterDropdownsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
