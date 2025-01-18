import {Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-lookup-services/lookup.service";
import {catchError, of} from "rxjs";
import {FormGroup} from "@angular/forms";

@Component({
  selector: 'app-subcategory-dropdown',
  standalone: false,

  templateUrl: './subcategory-dropdown.component.html',
  styleUrl: './subcategory-dropdown.component.css'
})
export class SubcategoryDropdownComponent implements OnInit{
  @Input() parentForm!: FormGroup;
  subcategories: {subcategoryId: number, subcategoryName: string, gameplayCategoryName: string}[] = [];
  filteredSubcategories: {subcategoryId: number, subcategoryName: string, gameplayCategoryName: string}[] = [];

  constructor(private lookupService: LookupService) { }

  ngOnInit() {
    this.fetchSubcategories();

    // Subscribing to gameplay category selection changes to filter subcategories by parent category
    this.parentForm.get('category')?.valueChanges.subscribe(value => {

      //clearing filtered subcategory array after value change and resetting dropdown
      this.parentForm.get('subcategory')?.reset();
      this.parentForm.get('subcategory')?.disable();
      this.filteredSubcategories.splice(0, this.filteredSubcategories.length);

      //filtering subcategory options by selected category
      if (value != null && value.gameplayCategoryName != 'Other') {
        this.filteredSubcategories = this.subcategories.filter(
          subcategory =>
              subcategory.gameplayCategoryName === value.gameplayCategoryName
        );
        if (this.filteredSubcategories.length > 0) {
          this.parentForm.get('subcategory')?.enable();
        }
        else {
          this.parentForm.get('subcategory')?.reset();
          this.parentForm.get('subcategory')?.disable();
        }
      }
      else {
        this.parentForm.get('subcategory')?.reset();
        this.parentForm.get('subcategory')?.disable();
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
        console.log('Gameplay subcategories dropdown options fetched', this.subcategories);
      });
  }
}
