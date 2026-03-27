import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';
import { MatMenuModule } from '@angular/material/menu';
import { SearchBarComponent } from './search-bar.component';
import { FilterService } from '../../../services/api-services/filter-api/filter.service';
import { HiddenListingsApiService } from '../../../services/api-services/hidden-listings-api/hidden-listings-api.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';

describe('SearchBarComponent', () => {
  let component: SearchBarComponent;
  let fixture: ComponentFixture<SearchBarComponent>;

  beforeEach(async () => {
    const filterSpy = jasmine.createSpyObj('FilterService', ['pushStoredState', 'pullState', 'update', 'clearFilters'], {
      state$: of({ searchInput: '', server: null, environment: null, experience: null, category: null,
                   subcategory: null, legality: null, pvpStatus: null, planetarySystem: null,
                   planetMoon: null, groupStatus: null, language: null })
    });
    filterSpy.pullState.and.returnValue({ searchInput: '', server: null, environment: null, experience: null,
      category: null, subcategory: null, legality: null, pvpStatus: null, planetarySystem: null,
      planetMoon: null, groupStatus: null, language: null });
    const hiddenApiSpy = jasmine.createSpyObj('HiddenListingsApiService', ['clearHidden']);
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    const dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      declarations: [SearchBarComponent],
      imports: [MatMenuModule],
      providers: [
        { provide: FilterService, useValue: filterSpy },
        { provide: HiddenListingsApiService, useValue: hiddenApiSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(SearchBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
