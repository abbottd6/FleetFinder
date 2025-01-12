import {Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/lookup.service";
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
  subcategories: {subcategoryId: number, subcategoryName: string}[] = [];

  constructor(private lookupService: LookupService) { }

  ngOnInit() {
    this.fetchSubcategories();
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
