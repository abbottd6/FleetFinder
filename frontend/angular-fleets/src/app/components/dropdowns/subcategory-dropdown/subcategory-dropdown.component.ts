import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-services/reference-data-api/lookup.service";
import {catchError, of, Subject} from "rxjs";
import {FormControl} from "@angular/forms";
import {environment} from "../../../../environments/environment";
import {GameplaySubcategory} from "../../../models/reference-data/reference-data.models";

@Component({
  selector: 'app-subcategory-dropdown',
  standalone: false,

  templateUrl: './subcategory-dropdown.component.html',
  styleUrl: './subcategory-dropdown.component.css'
})
export class SubcategoryDropdownComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() altLabel: string | null = null;
  @Input() subcategoryControl!: FormControl;
  @Input() categoryControl!: FormControl;
  subcategories: GameplaySubcategory[] = [];
  filteredSubcategories: GameplaySubcategory[] = [];

  constructor(private lookupService: LookupService) { }

  ngOnInit() {
    this.categoryControl?.valueChanges.subscribe(value => {
      this.subcategoryControl.reset();
      this.applySubcatFilter(value);
    });
    this.fetchSubcategories();

    // Subscribing to gameplay category selection changes to filter subcategories by parent category

  }

  fetchSubcategories() {
    this.lookupService.getGameplaySubcategories()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown gameplay subcategories:', err);
          return of([])
        })
      )
      .subscribe(data => {this.subcategories = data;

        const currentCategory = this.categoryControl?.value;
        if (currentCategory != null) {
          this.applySubcatFilter(currentCategory);
        }
        if(!environment.production) {
          console.log('Gameplay subcategories dropdown options fetched', this.subcategories);
        }
      });
  }

  applySubcatFilter(cat: number) {
    // console.warn("CATEGORY CHANGED")
    // console.log("CATEGORY: ", value)
    //clearing filtered subcategory array after value change and resetting dropdown
    if (cat == null || cat === 12) {
      this.subcategoryControl?.reset();
      this.subcategoryControl?.disable();
      this.filteredSubcategories.splice(0, this.filteredSubcategories.length);
    }
    //filtering subcategory options by selected category
    if (cat != null && cat != 12) {
      this.filteredSubcategories = this.subcategories.filter(
        subcategory =>
          subcategory.gameplayCategoryId === cat
      );
      if (this.filteredSubcategories.length > 0) {
        // console.log("Filtered subcategories: ", this.filteredSubcategories);
        // console.warn("UPDATE HAPPENS HERE")
        this.subcategoryControl?.enable();
      } else {
        this.subcategoryControl?.reset();
        this.subcategoryControl?.disable();
      }
    } else {
      this.subcategoryControl?.reset();
      this.subcategoryControl?.disable();
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
