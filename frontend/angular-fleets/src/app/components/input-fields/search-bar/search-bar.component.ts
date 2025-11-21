import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {LookupService} from "../../../services/api-lookup-services/lookup.service";

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

  @Output() search = new EventEmitter<string>();
  @Output() filters = new EventEmitter<string[]>();

  filterCategories: string[] = ['Group Status', 'Server Region', 'Environment', 'Experience',
    'Gameplay Category', 'Star System', 'PvP Status', 'Legality', 'Comms Options', 'Play Style', 'Schedule']

  constructor(private lookup: LookupService) {}

  ngOnInit() {
    this.principalCtrl?.valueChanges.subscribe( value => {
      this.parentCtrl.reset();
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
}

