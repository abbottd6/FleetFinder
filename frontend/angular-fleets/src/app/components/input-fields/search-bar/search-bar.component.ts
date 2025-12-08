import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {map, Observable, of, shareReplay, Subject, take, takeUntil} from 'rxjs';
import {
  FilterOptionKey,
  filterOptions,
  FilterService,
  ListingFilterState
} from "../../../services/api-lookup-services/filter.service";
import {MAT_DATE_FORMATS} from "@angular/material/core";
import {EVENT_RANGE_FORMATS} from "../../../models/event-range-formats";
import {HiddenListingsService} from "../../../services/user-services/hidden-listings.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {BreakpointObserver} from "@angular/cdk/layout";

// interface for creating the primary filter options
export interface FilterPrincipal {
  value: FilterOptionKey;
  label: string;
}

export type LayoutMode = 'handheld' | 'mobile' | 'full';

@Component({
  selector: 'app-search-bar',
  standalone: false,
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css',
  providers: [
    {provide: MAT_DATE_FORMATS, useValue: EVENT_RANGE_FORMATS }
  ]
})
export class SearchBarComponent implements OnInit, OnChanges, OnDestroy {
  @Input() submittedState: ListingFilterState | null = null;
  private breakpointObserver = inject(BreakpointObserver);

  private destroy$ = new Subject<void>();

  filtersMatch = true;
  principalCtrl = new FormControl<FilterOptionKey | null>(null);
  parentCtrl = new FormControl<filterOptions | null>({ value: null, disabled: true });
  childCtrl = new FormControl<filterOptions | null>({ value: null, disabled: true});
  dateRange = new FormGroup({
    start: new FormControl<Date | null>(null),
    end: new FormControl<Date | null>(null),
  })

  displayedFilters$!: Observable<{ key: FilterOptionKey; value: any}[]>;

  @Output() applySearchAndFilters = new EventEmitter<ListingFilterState>();

  readonly FILTER_CATEGORIES: FilterPrincipal[] = [
    { value: 'server', label: 'Server Region' },
    { value: 'environment', label: 'Environment' },
    { value: 'experience', label: 'Experience' },
    { value: 'category', label: 'Gameplay Category' },
    { value: 'groupStatus', label: 'Date/Current' },
    { value: 'pvpStatus', label: 'PvP Status' },
    { value: 'legality', label: 'Legality' },
    { value: 'system', label: 'Star System' },
    { value: 'commsOption', label: 'Comms Options' },
    { value: 'playStyle', label: 'Play Style' },
  ]

  readonly COMMS_OPTIONS: filterOptions[] = [
    { id: 1, option: 'Required'},
    { id: 2, option: 'Optional'},
    { id: 3, option: 'No Comms'},
  ];

  parentFilters$!: Observable<filterOptions[]>;
  childFilters$!: Observable<filterOptions[]>;

  constructor(private filter: FilterService, private hideService: HiddenListingsService,
              private snackBar: MatSnackBar) {
    this.maxDate.setMonth(this.maxDate.getMonth() +6);
  }

  ngOnInit() {
    this.principalCtrl?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe( value => {
      this.parentCtrl.reset();
      this.getParentOptions();
    })

    this.parentCtrl.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe( value => {
      this.childCtrl.reset();
      this.getChildOptions();
    })

    this.displayedFilters$ = this.filter.state$.pipe(
      map(state =>
        Object.entries(state)
          .filter(([field, value]) => value !== null && value !== '' && field != 'searchInput')
          .map(([key, value]) => ({ key: key as FilterOptionKey, value: value })),)
    )

    this.filter.state$.pipe(takeUntil(this.destroy$))
      .subscribe(state => {
      this.compareStates(state, this.submittedState)
    })
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.compareStates(this.filter.pullState(), this.submittedState)
  }

  emitSearchAndFilter(search: string): void {
    this.filter.update('searchInput', search || null);
    const state = this.filter.pullState()
    this.applySearchAndFilters.emit(state);
    console.log("is this working? ", this.filtersMatch);
  }

  onKeyUp(event: KeyboardEvent): void {
    const value = (event.target as  HTMLInputElement).value;
    this.filter.update('searchInput', value || null);
  }

  inputEnterPress(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.emitSearchAndFilter(value);
  }

  clearSearch(input: HTMLInputElement): void {
    input.value = '';
    this.filter.update('searchInput', null);
    this.emitSearchAndFilter(input.value);
  }

  addFilter(): void {
    if(!this.principalCtrl.value || !this.parentCtrl.value) {
      return
    }

    const principal: FilterOptionKey = this.principalCtrl.value;
    const parent = this.parentCtrl.value;

    this.filter.updateOption(principal, parent)

    if(!this.childCtrl.value && !this.dateRange.value) {
      this.principalCtrl.reset();
      this.parentCtrl.reset();
      this.childCtrl.reset();
      return
    }

    switch(principal) {
      case 'category':
        this.filter.update('subcategory', this.childCtrl.value);
        break;
      case 'system':
        this.filter.update('planetMoonSystem', this.childCtrl.value);
        break;
      case 'groupStatus':
        if(this.dateRange.value.start != null && this.dateRange.value.end != null) {
          this.filter.update('dateStart', this.toDateOnly(this.dateRange.value.start));
          this.filter.update('dateEnd', this.toDateOnly(this.dateRange.value.end));
          this.dateRange.reset();
        }
        break;
    }

    this.principalCtrl.reset();
    this.parentCtrl.reset();
    this.childCtrl.reset();
    console.log("is this working? ", this.filtersMatch);
  }

  // converting dateStart and dateEnd for date range to just dates instead of timestamps/zone/etc.
  toDateOnly = (filterDate: Date | null): string | null => {
    return filterDate ? filterDate.toISOString().substring(0,10) : null;
  };

  // clear users hidden listings (backend filter)
  clearHiddenListings(searchInput: string) {
    this.hideService.clearHidden().pipe(takeUntil(this.destroy$)).subscribe({
      next: (response: {Response: string}) =>
        this.snackBar.open(`${response.Response}`, 'OK', {
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['mobile-snackbar']
        }),
      complete: () => {
        this.emitSearchAndFilter(searchInput);
      }
    })
  }

  undoLastHide(searchInput: string) {
    this.hideService.undoLastHide().pipe(takeUntil(this.destroy$)).subscribe({
      next: (response: {Response: string}) =>
        this.snackBar.open(`${response.Response}`, 'OK', {
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['mobile-snackbar']
        }),
      complete: () => {
        this.emitSearchAndFilter(searchInput);
      }
    })
  }

  // remove individual filter from chips
  removeFilter(value: FilterOptionKey): void {
    this.filter.updateOption(value, null);
    console.log("is this working? ", this.filtersMatch);
  }

  // clear all filters chips, resets all to null, including search and resubmits search
  clearFilters(searchInput: string) {
    this.filter.clearFilters();
    this.emitSearchAndFilter(searchInput);
  }

  // function for picking the field to display in the filters chips
  displayFilterValue(value: any): string {
    if(!value) return '';
    if(typeof value === 'object') {
      if ('option' in value) return value.option;
    }
    return String(value);
  }

  compareStates(submitted: ListingFilterState, local: ListingFilterState | null): boolean {
    return this.filtersMatch = JSON.stringify(submitted) === JSON.stringify(local);
  }

  //populates the second dropdown based on values from the first dropdown
  getParentOptions() {
    switch(this.principalCtrl.value) {
      case 'groupStatus':
        this.parentFilters$ = this.filter.filterGroupStatus();
        this.parentCtrl.enable();
        break;
      case 'server':
        this.parentFilters$ = this.filter.filterServerRegions();
        this.parentCtrl.enable();
        break;
      case 'environment':
        this.parentFilters$ = this.filter.filterEnvironments();
        this.parentCtrl.enable();
        break;
      case 'experience':
        this.parentFilters$ = this.filter.filterExperiences();
        this.parentCtrl.enable();
        break;
      case 'category':
        this.parentFilters$ = this.filter.filterCategories();
        this.parentCtrl.enable();
        break;
      case 'system':
        this.parentFilters$ = this.filter.filterSystems();
        this.parentCtrl.enable();
        break;
      case 'pvpStatus':
        this.parentFilters$ = this.filter.filterPvp();
        this.parentCtrl.enable();
        break;
      case 'legality':
        this.parentFilters$ = this.filter.filterLegalities();
        this.parentCtrl.enable();
        break;
      case 'commsOption':
        this.parentFilters$ = of(this.COMMS_OPTIONS);
        this.parentCtrl.enable();
        break;
      case 'playStyle':
        this.parentFilters$ = this.filter.filterPlayStyles();
        this.parentCtrl.enable();
        break;
      default:
        this.parentCtrl.disable();
    }
  }

  // populates the third dropdown based on values from the first two
  getChildOptions() {
    switch(this.principalCtrl.value) {
      case 'category':
        if (this.parentCtrl.value != null) {
          this.childCtrl.enable();
          this.childFilters$ = this.filter.filterSubcategories(this.parentCtrl.value.id);
        }
        break;
      case 'system':
        if (this.parentCtrl.value != null) {
          this.childCtrl.enable();
          this.childFilters$ = this.filter.filterPlanets(this.parentCtrl.value.id);
        }
        break;
      case 'groupStatus':
        if(this.parentCtrl.value?.option == 'Future/Scheduled') {
          this.childCtrl.disable();
          this.dateRange.enable();
        }
        break;
      default:
        this.childCtrl.disable()
    }
  }

  //variable for disabling dates prior to current date
  minDate: Date = new Date();

  //variable for disabling dates more than 6 months ahead
  maxDate: Date = new Date();

  //method for defining the dates that should be disabled in the datepicker (previous dates, distant future dates)
  disabledDatesClass = (date: Date): string => {
    const currDate = new Date();
    currDate.setHours(0, 0, 0, 0);
    return date < currDate ? 'disabled-date' : '';
  };

  layoutMode$: Observable<LayoutMode> = this.breakpointObserver
    .observe([
      '(max-width: 500px)',
      '(min-width: 501px) and (max-width: 1050px)',
      '(min-width: 1051px)'
    ])
    .pipe(
      map(state => {
        if (state.breakpoints['(max-width: 500px)']) {
          return 'handheld';
        }
        if (state.breakpoints['(min-width: 501px) and (max-width: 1050px)']) {
          return 'mobile';
        }

        return 'full';
      }),
      shareReplay(1)
    );
}

