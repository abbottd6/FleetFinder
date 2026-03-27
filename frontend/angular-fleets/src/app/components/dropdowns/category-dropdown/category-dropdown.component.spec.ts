import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { throwError } from 'rxjs';
import { CategoryDropdownComponent } from './category-dropdown.component';
import { LookupService } from '../../../services/api-services/reference-data-api/lookup.service';
import { GameplayCategory } from '../../../models/reference-data/reference-data.models';

const mockCategories: GameplayCategory[] = [{ gameplayCategoryId: 1, gameplayCategoryName: 'Combat' }];

describe('CategoryDropdownComponent', () => {
  let component: CategoryDropdownComponent;
  let fixture: ComponentFixture<CategoryDropdownComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CategoryDropdownComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, NgSelectModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(CategoryDropdownComponent);
    component = fixture.componentInstance;
    component.categoryControl = new FormControl<number | null>(null);
    fixture.detectChanges();
    httpMock.expectOne(req => req.url.includes('/lookup/gameplay-categories')).flush(mockCategories);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch categories on init and populate categories array', () => {
    expect(component.categories.length).toBe(1);
    expect(component.categories[0].gameplayCategoryName).toBe('Combat');
  });

  it('should fall back to empty array on error', () => {
    const ls = TestBed.inject(LookupService);
    spyOn(ls, 'getGameplayCategories').and.returnValue(throwError(() => new Error('err')));
    component.categories = [];
    component.fetchCategories();
    expect(component.categories.length).toBe(0);
  });
});
