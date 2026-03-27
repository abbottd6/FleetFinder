import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { of } from 'rxjs';

import { SubcategoryDropdownComponent } from './subcategory-dropdown.component';
import { LookupService } from '../../../services/api-services/reference-data-api/lookup.service';
import { GameplaySubcategory } from '../../../models/reference-data/reference-data.models';

const mockSubcategories: GameplaySubcategory[] = [
  { subcategoryId: 1, subcategoryName: 'Prospecting', gameplayCategoryId: 3, gameplayCategoryName: 'Mining' },
  { subcategoryId: 2, subcategoryName: 'Salvage',     gameplayCategoryId: 4, gameplayCategoryName: 'Salvaging' },
];

describe('SubcategoryDropdownComponent', () => {
  let component: SubcategoryDropdownComponent;
  let fixture: ComponentFixture<SubcategoryDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubcategoryDropdownComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, NgSelectModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(SubcategoryDropdownComponent);
    component = fixture.componentInstance;
    component.categoryControl = new FormControl<number | null>(null);
    component.subcategoryControl = new FormControl<number | null>({ value: null, disabled: true });

    // Spy on the real service instance before detectChanges triggers ngAfterViewInit
    const lookupService = TestBed.inject(LookupService);
    spyOn(lookupService, 'getGameplaySubcategories').and.returnValue(of(mockSubcategories));

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch subcategories on init and populate subcategories array', () => {
    expect(component.subcategories.length).toBe(2);
    expect(component.subcategories[0].subcategoryName).toBe('Prospecting');
  });

  it('applySubcatFilter() should filter subcategories by gameplayCategoryId', () => {
    component.applySubcatFilter(3);
    expect(component.filteredSubcategories.length).toBe(1);
    expect(component.filteredSubcategories[0].subcategoryName).toBe('Prospecting');
  });

  it('applySubcatFilter() should enable the control when matching subcategories exist', () => {
    component.applySubcatFilter(3);
    expect(component.subcategoryControl.enabled).toBeTrue();
  });

  it('applySubcatFilter() should show no subcategories and disable control when category is null', () => {
    component.applySubcatFilter(null as any);
    expect(component.filteredSubcategories.length).toBe(0);
    expect(component.subcategoryControl.disabled).toBeTrue();
  });

  it('applySubcatFilter() should disable the control when category is 12 (no subcategories)', () => {
    component.applySubcatFilter(12);
    expect(component.filteredSubcategories.length).toBe(0);
    expect(component.subcategoryControl.disabled).toBeTrue();
  });
});
