import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {BehaviorSubject, Subject, takeUntil} from "rxjs";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {AsyncPipe, NgForOf} from "@angular/common";
import {PublicSocialApiService} from "../../../services/api-services/public-social-api/public-social-api.service";
import {PublicUser} from "../../../models/public-user/public-user";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from "@angular/material/autocomplete";
import {MatTooltip} from "@angular/material/tooltip";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-search-input-auto-complete',
  standalone: true,
  templateUrl: './search-input-auto-complete.component.html',
  imports: [
    MatFormField,
    ReactiveFormsModule,
    MatInput,
    MatAutocomplete,
    MatOption,
    NgForOf,
    MatLabel,
    AsyncPipe,
    MatAutocompleteTrigger
  ],
  styleUrl: './search-input-auto-complete.component.css'
})

export class SearchInputAutoCompleteComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() abstractCtrl!: FormControl;
  @Input() fieldLabel?: string;
  @Input() errorMessage?: string;
  @Input() placeholderLabel?: string;

  private searchResultsSubject = new BehaviorSubject<PublicUser[]>([]);
  protected searchResults$ = this.searchResultsSubject.asObservable();
  protected noResults: boolean = true;


  constructor(private socialApi: PublicSocialApiService){};

  ngOnInit() {
  }

  searchUsers() {
    const term = this.abstractCtrl.value;
    this.socialApi.searchUsers(term).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (page) => {
          this.searchResultsSubject.next(page.content);
          this.noResults = page.content.length == 0;
          console.log()
        }
      })
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
