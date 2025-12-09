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
import {MatDialog} from "@angular/material/dialog";
import {MobileFiltersPopupComponent} from "../../pop-ups/mobile-filters-popup/mobile-filters-popup.component";

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
  displayedFilters$!: Observable<{ key: FilterOptionKey; value: any}[]>;

  @Output() applySearchAndFilters = new EventEmitter<ListingFilterState>();

  constructor(private filter: FilterService, private hideService: HiddenListingsService,
              private snackBar: MatSnackBar, private dialog: MatDialog) {
  }

  ngOnInit() {
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

  ngOnChanges(changes: SimpleChanges): void {
    this.compareStates(this.filter.pullState(), this.submittedState)
  }

  emitSearchAndFilter(search: string): void {
    this.filter.update('searchInput', search || null);
    const state = this.filter.pullState()
    this.applySearchAndFilters.emit(state);
  }

  emitFilterState(): void {
    const state = this.filter.pullState();
    this.applySearchAndFilters.emit(state);
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

  // remove individual filter from chips
  removeFilter(value: FilterOptionKey): void {
    this.filter.updateOption(value, null);
    this.emitFilterState()
  }

  // clear all filters chips, resets all to null, including search and resubmits search
  clearFilters() {
    this.filter.clearFilters();
    this.emitFilterState();
  }

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

  openMobileFilters(): void {
    const dialogRef = this.dialog.open(MobileFiltersPopupComponent, {
      minWidth: '75vw',
      maxWidth: '95vw',
      minHeight: '50vh',
      panelClass: ['mobile-filters-popup'],
      data: {
        currentFilters: this.displayedFilters$,
      }
    });

    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      if(result == true) {
        this.emitFilterState();
      }
    })
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

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

