import {Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-lookup-services/lookup.service";
import {catchError, of} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-category-dropdown',
  standalone: false,

  templateUrl: './category-dropdown.component.html',
  styleUrl: './category-dropdown.component.css'
})
export class CategoryDropdownComponent implements OnInit{
  @Input() categoryControl!: FormControl;
  categories: {gameplayCategoryId: number, gameplayCategoryName: string}[] = [];

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
