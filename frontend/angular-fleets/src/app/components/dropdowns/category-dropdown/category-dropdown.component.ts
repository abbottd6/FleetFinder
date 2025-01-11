import {Component, OnInit} from '@angular/core';
import {LookupService} from "../../../services/lookup.service";
import {catchError, of} from "rxjs";

@Component({
  selector: 'app-category-dropdown',
  standalone: false,

  templateUrl: './category-dropdown.component.html',
  styleUrl: './category-dropdown.component.css'
})
export class CategoryDropdownComponent implements OnInit{
  categories: {gameplayCategoryId: number, gameplayCategoryName: string, gameplaySubcategories:
      {subcategoryId: number, subcategoryName: string, gameplayCategoryName: string}}[] = [];

  constructor(private lookupService: LookupService) {}

  ngOnInit() {
    this.fetchCategories();
  }

  fetchCategories() {
    this.lookupService.getGameplayCategories()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown gameplay categories:', err);
          return of([]);
        })
      )
      .subscribe((data) => {this.categories = data;
        console.log('Gameplay categories dropdown options fetched:', this.categories);
      });
  }
}
