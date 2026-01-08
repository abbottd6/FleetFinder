import {Component, EventEmitter, inject, Inject, Output, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {BreakpointObserver} from "@angular/cdk/layout";
import {map, Observable, shareReplay} from "rxjs";
import {FilterOptionKey, FilterService} from "../../../services/api-services/filter-api/filter.service";
import {FilterDropdownsComponent} from "../../dropdowns/filter-dropdowns/filter-dropdowns.component";

@Component({
  selector: 'app-listing-view-mobile-filters-popup',
  standalone: false,
  templateUrl: './mobile-filters-popup.component.html',
  styleUrls: [
    './mobile-filters-popup.component.css',
    '../../../../styles.css'
  ],
})
export class MobileFiltersPopupComponent {

  private breakpointObserver: BreakpointObserver = inject(BreakpointObserver);

  @ViewChild('dropdowns') dropdowns!: FilterDropdownsComponent;

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: {
      currentFilters: Observable<{ key: FilterOptionKey; value: any}[]>
    },
    private dialogRef: MatDialogRef<MobileFiltersPopupComponent>,
    private filter: FilterService,
  ) {}

  onConfirm(): void {
    this.dropdowns?.addFilter();
    this.dialogRef.close(true);
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  displayFilterValue(value: any): string {
    if(!value) return '';
    if(typeof value === 'object') {
      if ('option' in value) return value.option;
    }
    return String(value);
  }

  // remove individual filter from chips
  removeFilter(value: FilterOptionKey): void {
    this.filter.updateOption(value, null);
  }

  // clear all filters chips, resets all to null, including search and resubmits search
  clearFilters() {
    this.filter.clearFilters();
  }

  isHandheld$ = this.breakpointObserver
      .observe('(max-width: 1050px)')
      .pipe(map(result => result.matches),
        shareReplay());
}
