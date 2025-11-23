import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {map, Observable, of} from 'rxjs';
import {
  FilterOptionKey,
  filterOptions,
  FilterService,
  ListingFilterState
} from "../../../services/api-lookup-services/filter.service";

// interface for creating the primary filter options
export interface FilterPrincipal {
  value: FilterOptionKey;
  label: string;
}

@Component({
  selector: 'app-search-bar',
  standalone: false,
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent implements OnInit{
  principalCtrl = new FormControl<FilterOptionKey | null>(null);
  parentCtrl = new FormControl<filterOptions | null>({ value: null, disabled: true });
  childCtrl = new FormControl<filterOptions | null>({ value: null, disabled: true});

  displayedFilters$!: Observable<{ key: FilterOptionKey; value: any}[]>;

  @Output() applySearchAndFilters = new EventEmitter<ListingFilterState>();

  readonly FILTER_CATEGORIES: FilterPrincipal[] = [
    { value: 'groupStatus', label: 'Group Status' },
    { value: 'server', label: 'Server Region' },
    { value: 'environment', label: 'Environment' },
    { value: 'experience', label: 'Experience' },
    { value: 'category', label: 'Gameplay Category' },
    { value: 'system', label: 'Star System' },
    { value: 'pvpStatus', label: 'PvP Status' },
    { value: 'legality', label: 'Legality' },
    { value: 'commsOption', label: 'Comms Options' },
    { value: 'playStyle', label: 'Play Style' },
    { value: 'scheduleDate', label: 'Schedule' },
  ]

  readonly COMMS_OPTIONS: filterOptions[] = [
    { id: 1, option: 'Required'},
    { id: 2, option: 'Optional'},
    { id: 3, option: 'No Comms'},
  ];

  parentFilters$!: Observable<filterOptions[]>;
  childFilters$!: Observable<filterOptions[]>;

  constructor(private filter: FilterService) {}

  ngOnInit() {
    this.principalCtrl?.valueChanges.subscribe( value => {
      this.parentCtrl.reset();
      this.getParentOptions();
    })

    this.parentCtrl.valueChanges.subscribe( value => {
      this.childCtrl.reset();
      this.getChildOptions();
    })

    this.displayedFilters$ = this.filter.state$.pipe(
      map(state =>
        Object.entries(state)
          .filter(([field, value]) => value !== null && value !== '' && field != 'searchInput')
          .map(([key, value]) => ({ key: key as FilterOptionKey, value: value })),)
    )
  }

  emitSearchAndFilter(search: string): void {
    this.filter.update('searchInput', search || null);

    const state = this.filter.pullState()

    this.applySearchAndFilters.emit(state);
  }

  clearSearch(input: HTMLInputElement): void {
    input.value = '';
  }

  addFilter(): void {
    if(!this.principalCtrl.value || !this.parentCtrl.value) {
      return
    }

    const principal: FilterOptionKey = this.principalCtrl.value;
    const parent = this.parentCtrl.value;

    this.filter.updateOption(principal, parent)

    if(!this.childCtrl.value) {
      this.principalCtrl.reset();
      this.parentCtrl.reset();
      this.childCtrl.reset();
      return
    }

    switch(principal) {
      case 'category':
        this.filter.update('subcategory', this.childCtrl.value)
        break;
      case 'system':
        this.filter.update('planetMoonSystem', this.childCtrl.value)
        break;
    }

    this.principalCtrl.reset();
    this.parentCtrl.reset();
    this.childCtrl.reset();
  }

  removeFilter(value: FilterOptionKey): void {
    this.filter.updateOption(value, null);
  }

  clearFilters(searchInput: string) {
    this.filter.clearFilters();
    this.emitSearchAndFilter(searchInput);
  }

  displayFilterValue(value: any): string {
    if(!value) return '';
    if('option' in value) return value.option;
    return String(value);
  }

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

  getChildOptions() {
    switch(this.principalCtrl.value) {
      case 'category':
        if (this.parentCtrl.value != null) {
          this.childCtrl.enable()
          this.childFilters$ = this.filter.filterSubcategories(this.parentCtrl.value.id);
        }
        break;
      case 'system':
        if (this.parentCtrl.value != null) {
          this.childCtrl.enable()
          this.childFilters$ = this.filter.filterPlanets(this.parentCtrl.value.id);
        }
        break;
      default:
        this.childCtrl.disable()
    }
  }
}

