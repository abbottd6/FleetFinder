import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormControl} from "@angular/forms";
import {async, concat, Observable, of} from 'rxjs';
import {filterChildOptions, filterOptions, FilterService} from "../../../services/api-lookup-services/filter.service";

export interface ListingsFilterState {
  search: string;
  filters: string[];
}

export interface FilterPrincipal {
  value: string;
  label: string;
}

@Component({
  selector: 'app-search-bar',
  standalone: false,
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent implements OnInit{
  principalCtrl = new FormControl<string | null>(null);
  parentCtrl = new FormControl<string | null>({ value: null, disabled: true });
  childCtrl = new FormControl<string | null>({ value: null, disabled: true});

  selectedFilters: string[] = [];
  displayedFilters: string[] = [];

  private filterState: ListingsFilterState = {
    search: '',
    filters: []
  }

  @Output() applySearchAndFilters = new EventEmitter<ListingsFilterState>();

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
    { value: 'eventSchedule', label: 'Schedule' },
  ]

  readonly COMMS_OPTIONS: filterOptions[] = [
    { id: 1, option: 'Required'},
    { id: 2, option: 'Optional'},
    { id: 3, option: 'No Comms'},
  ];

  parentFilters$!: Observable<filterOptions[]>;
  childFilters$!: Observable<filterChildOptions[]>;
  parentLabel: string | null = null;
  childLabel: string | null = null;


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

    this.childCtrl.valueChanges.subscribe( value => {

    })
  }

  emitSearchAndFilter(searchInput: string): void {
    this.filterState = {
      search: searchInput.trim().toLowerCase(),
      filters: this.selectedFilters,
    }
    console.log(this.filterState);
    this.applySearchAndFilters.emit(this.filterState);
  }

  clearSearch(input: HTMLInputElement): void {
    input.value = '';
  }

  addFilter(principal: string | null, parent: string | null, child: string | null): void {
    const tempFilter = [principal, parent, child]
      .filter(val => val != null && val != '')
      .join(':');

    const addIfNew = (value: string) => {
      if (value == null) return;

      if(principal != null && principal != '') {
        const alreadyExists = this.selectedFilters.some(
          f => f.includes(principal));
        if (!alreadyExists) {
          this.selectedFilters.push(value);
          if (this.parentLabel != null) {
            this.displayedFilters.push(this.parentLabel);
          }
          if (this.childLabel != null) {
            this.displayedFilters.push(this.childLabel);
          }
          this.principalCtrl.reset();
          this.parentCtrl.reset();
          this.childCtrl.reset();
        }
      }
    };

    addIfNew(tempFilter);
    console.log(this.selectedFilters)
  }

  onParentChange(option: filterOptions | null): void {
    this.parentLabel = option?.option ?? null;
  }

  onChildChange(option: filterChildOptions | null): void {
    this.childLabel = option?.option ?? null;
  }

  clearFilters(searchInput: string) {
    this.selectedFilters = [];
    this.displayedFilters = [];
    this.emitSearchAndFilter(searchInput);
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
        this.childCtrl.enable()
        this.childFilters$ = this.filter.filterSubcategories(this.parentCtrl.value);
        break;
      case 'system':
        this.childCtrl.enable()
        this.childFilters$ = this.filter.filterPlanets(this.parentCtrl.value);
        break;
      default:
        this.childCtrl.disable()
    }
  }
}

