import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-services/reference-data-api/lookup.service";
import {catchError, of, Subject} from "rxjs";
import {FormControl} from "@angular/forms";
import {environment} from "../../../../environments/environment";
import {GameplayCategory} from "../../../models/reference-data/reference-data.models";

@Component({
  selector: 'app-category-dropdown',
  standalone: false,

  templateUrl: './category-dropdown.component.html',
  styleUrl: './category-dropdown.component.css'
})
export class CategoryDropdownComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() categoryControl!: FormControl;
  categories: GameplayCategory[] = [];

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
        if(!environment.production) {
          console.log('Gameplay categories dropdown options fetched:', this.categories);
        }
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
