import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubcategoryDropdownComponent } from './subcategory-dropdown.component';

describe('SubcategoryDropdownComponent', () => {
  let component: SubcategoryDropdownComponent;
  let fixture: ComponentFixture<SubcategoryDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubcategoryDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SubcategoryDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
