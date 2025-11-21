import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormControl} from "@angular/forms";
import {Observable, of} from 'rxjs';
import {FilterService} from "../../../services/api-lookup-services/filter.service";

export interface ListingsFilterState {
  search: string;
  filters: string[];
}

@Component({
  selector: 'app-search-bar',
  standalone: false,
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent implements OnInit{
  principalCtrl = new FormControl<string | null>(null);
  parentCtrl = new FormControl<string | null>(null);
  childCtrl = new FormControl<string | null>(null);

  /* TO DO
  combine these into a single object to pass back to the parent component
   */
  @Output() search = new EventEmitter<string>();
  @Output() filters = new EventEmitter<string[]>();

  filterCategories: string[] = ['Group Status', 'Server Region', 'Environment', 'Experience',
    'Gameplay Category', 'Star System', 'PvP Status', 'Legality', 'Comms Options', 'Play Style', 'Schedule']
  readonly COMMS_OPTIONS: string[] = ['Required', 'Optional', 'No Comms'];
  parentFilters$!: Observable<string[]>;

  constructor(private filter: FilterService) {}

  ngOnInit() {
    this.principalCtrl?.valueChanges.subscribe( value => {
      this.parentCtrl.reset();
      this.getParentOptions();
    })

    this.parentCtrl.valueChanges.subscribe( value => {
      this.childCtrl.reset();
      this.emitFilters();
    })

    this.childCtrl.valueChanges.subscribe( value => {
      this.emitFilters();
    })
  }

  onKeyup(event: Event): void {
    const input = (event.target as HTMLInputElement).value;
    this.search.emit(input);
  }

  emitFilters(): void {
    this.filters.emit([
      this.principalCtrl.value ?? '',
      this.parentCtrl.value ?? '',
      this.childCtrl.value ?? '',
    ]);
  }

  clearSearch(input: HTMLInputElement): void {
    input.value = '';
    this.search.emit('');
    this.onKeyup({ target: input } as unknown as Event);
  }

  getParentOptions() {
    switch(this.principalCtrl.value) {
      case 'Group Status':
        this.parentFilters$ = this.filter.filterGroupStatus();
        break;
      case 'Server Region':
        this.parentFilters$ = this.filter.filterServerRegions();
        break;
      case 'Environment':
        this.parentFilters$ = this.filter.filterEnvironments();
        break;
      case 'Experience':
        this.parentFilters$ = this.filter.filterExperiences();
        break;
      case 'Gameplay Category':
        this.parentFilters$ = this.filter.filterCategories();
        break;
      case 'Star System':
        this.parentFilters$ = this.filter.filterSystems();
        break;
      case 'PvP Status':
        this.parentFilters$ = this.filter.filterPvp();
        break;
      case 'Legality':
        this.parentFilters$ = this.filter.filterLegalities();
        break;
      case 'Comms Options':
        this.parentFilters$ = of(this.COMMS_OPTIONS);
        break;
      case 'Play Style':
        this.parentFilters$ = this.filter.filterPlayStyles();
        break;
    }
  }
}

