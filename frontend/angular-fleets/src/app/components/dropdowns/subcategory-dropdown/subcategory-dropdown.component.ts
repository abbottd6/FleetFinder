import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-lookup-services/lookup.service";
import {catchError, of} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-subcategory-dropdown',
  standalone: false,

  templateUrl: './subcategory-dropdown.component.html',
  styleUrl: './subcategory-dropdown.component.css'
})
export class SubcategoryDropdownComponent implements AfterViewInit{
  @Input() subcategoryControl!: FormControl;
  @Input() categoryControl!: FormControl;
  subcategories: {subcategoryId: number, subcategoryName: string, gameplayCategoryName: string}[] = [];
  filteredSubcategories: {subcategoryId: number, subcategoryName: string, gameplayCategoryName: string}[] = [];

  constructor(private lookupService: LookupService) { }

  ngAfterViewInit() {
    this.fetchSubcategories();

    // Subscribing to gameplay category selection changes to filter subcategories by parent category
    this.categoryControl?.valueChanges.subscribe(value => {

      //clearing filtered subcategory array after value change and resetting dropdown
      this.subcategoryControl?.reset();
      this.subcategoryControl?.disable();
      this.filteredSubcategories.splice(0, this.filteredSubcategories.length);

      //filtering subcategory options by selected category
      if (value != null && value.gameplayCategoryName != 'Other') {
        this.filteredSubcategories = this.subcategories.filter(
          subcategory =>
              subcategory.gameplayCategoryName === value.gameplayCategoryName
        );
        if (this.filteredSubcategories.length > 0) {
          this.subcategoryControl?.enable();
        }
        else {
          this.subcategoryControl?.reset();
          this.subcategoryControl?.disable();
        }
      }
      else {
        this.subcategoryControl?.reset();
        this.subcategoryControl?.disable();
      }
    })
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
        if(!environment.production) {
          console.log('Gameplay subcategories dropdown options fetched', this.subcategories);
        }
      });
  }
}
