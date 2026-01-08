import {Component, EventEmitter, inject, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {
  FilterOptionKey,
  filterOptions,
  FilterService,
  ListingFilterState
} from "../../../services/api-services/filter-api/filter.service";
import {FormControl, FormGroup} from "@angular/forms";
import {Observable, of, Subject, takeUntil} from "rxjs";

export interface FilterPrincipal {
  value: FilterOptionKey;
  label: string;
}

@Component({
  selector: 'app-filter-dropdowns',
  standalone: false,
  templateUrl: './filter-dropdowns.component.html',
  styleUrl: './filter-dropdowns.component.css'
})
export class FilterDropdownsComponent implements OnInit, OnDestroy {

  private destroy$: Subject<void> = new Subject<void>();

  @Output() applyFilters = new EventEmitter<ListingFilterState>();
  @Input() mobileApplyButtonCheck!: boolean;

  principalCtrl = new FormControl<FilterOptionKey | null>(null);
  parentCtrl = new FormControl<filterOptions | null>({ value: null, disabled: true });
  childCtrl = new FormControl<filterOptions | null>({ value: null, disabled: true});
  dateRange = new FormGroup({
    start: new FormControl<Date | null>(null),
    end: new FormControl<Date | null>(null),
  })

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

  constructor(private filter: FilterService){
    this.maxDate.setMonth(this.maxDate.getMonth() + 6);
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
    this.applyFilters.emit();
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

  // converting dateStart and dateEnd for date range to just dates instead of timestamps/zone/etc.
  toDateOnly = (filterDate: Date | null): string | null => {
    return filterDate ? filterDate.toISOString().substring(0,10) : null;
  };

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
