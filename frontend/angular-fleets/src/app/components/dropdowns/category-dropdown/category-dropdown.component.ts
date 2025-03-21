import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-lookup-services/lookup.service";
import {catchError, of} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-category-dropdown',
  standalone: false,

  templateUrl: './category-dropdown.component.html',
  styleUrl: './category-dropdown.component.css'
})
export class CategoryDropdownComponent implements AfterViewInit{
  @Input() categoryControl!: FormControl;
  categories: {gameplayCategoryId: number, gameplayCategoryName: string}[] = [];

  constructor(private lookupService: LookupService) {}

  ngAfterViewInit() {
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
        if(!environment.production) {
          console.log('Gameplay categories dropdown options fetched:', this.categories);
        }
      });
  }
}
