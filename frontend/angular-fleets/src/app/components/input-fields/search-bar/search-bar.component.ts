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
  parentLabel: filterOptions | null = null;
  childLabel: filterOptions | null = null;


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

  addFilter(): void {
    const delimiter = '**'

    const principal = this.principalCtrl.value ?? null;
    const parentId = this.parentLabel?.id ?? null;
    const parentVal = this.parentLabel?.option ?? null;
    const childId = this.childLabel?.id ?? null;
    const childVal = this.childLabel?.option ?? null;

    const parent = [parentId, parentVal]
      .filter(val => val != null && val != '')
      .join(delimiter);

    const child = [childId, childVal]
      .filter(val => val != undefined && val != '')
      .join(delimiter);

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
          this.parseForLabel(value);
          this.principalCtrl.reset();
          this.parentCtrl.reset();
          this.childCtrl.reset();
          this.parentLabel = null;
          this.childLabel = null;
        }
        if (alreadyExists) {
          this.replaceSingleFilter(value, principal)
          this.selectedFilters = this.selectedFilters.map(f =>
            f.includes(principal) ? value : f);
          this.principalCtrl.reset();
          this.parentCtrl.reset();
          this.childCtrl.reset();
          this.parentLabel = null;
          this.childLabel = null;
        }
      }
    };

    addIfNew(tempFilter);
    console.log(this.selectedFilters)
  }

  parseForLabel(criteria: string) {
    return criteria.split(':')
      .filter(el => el.includes('**'))
      .map(val => {
        const idx = val.indexOf('**');
        this.displayedFilters.push(val.slice(idx + 2))
      })

  }

  onParentChange(option: filterOptions | null): void {
    if(option) {
      this.parentLabel = {
        id: option.id,
        option: option.option,
      };
    }
  }

  onChildChange(option: filterChildOptions | null): void {
    if(option) {
      this.childLabel = {
        id: option.id,
        option: option.option,
      }
    }
  }

  clearFilters(searchInput: string) {
    this.selectedFilters = [];
    this.displayedFilters = [];
    this.emitSearchAndFilter(searchInput);
  }

  removeSingleFilter(thisFilter: string, searchInput: string) {
    const cleanedFilter = this.selectedFilters.filter(val => val.includes(thisFilter))
      .map(val => val.split(':'))
      .flat()
      .filter(part => !part.includes(thisFilter));

    const tempFilter = cleanedFilter.filter(val => val != null && val != '')
      .join(':');

    this.selectedFilters = this.selectedFilters.filter(val => !val.includes(thisFilter));
    this.selectedFilters.push(tempFilter);
    this.displayedFilters = this.displayedFilters.filter(val => !val.includes(thisFilter));
  }

  replaceSingleFilter(thisFilter: string, principal: string) {
    const existing = this.selectedFilters.filter(f => f.includes(principal))
      .map(val => val );

    const existingParts = existing.filter(val => val)
      .map(parts => {
        parts.split(':')
        .flat()
        .filter(part => part)
          .map(val => {
            const idx = val.indexOf('**');
            const lbl = val.substring(idx + 2);
          this.displayedFilters = this.displayedFilters.filter(el => !el.includes(lbl));
          })
      });

    this.parseForLabel(thisFilter);
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

