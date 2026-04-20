import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {BehaviorSubject, debounceTime, distinctUntilChanged, Subject, takeUntil} from "rxjs";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {PublicSocialApiService} from "../../../services/api-services/public-social-api/public-social-api.service";
import {PublicUser} from "../../../models/public-user/public-user";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from "@angular/material/autocomplete";
import {MatProgressBar} from "@angular/material/progress-bar";

export enum SearchStatus {
  NO_SEARCH,
  WORKING,
  COMPLETE
}

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
    MatAutocompleteTrigger,
    NgIf,
    MatProgressBar
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

  private searchReturnedSubject = new BehaviorSubject<SearchStatus>(SearchStatus.NO_SEARCH);
  protected searchReturned$ = this.searchReturnedSubject.asObservable();


  constructor(private socialApi: PublicSocialApiService){};

  ngOnInit() {
    this.abstractCtrl.valueChanges.pipe(
      takeUntil(this.destroy$),
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(value => {
      if (value) this.searchUsers();
      });
  }

  searchUsers() {
    this.searchReturnedSubject.next(SearchStatus.WORKING);
    const term = this.abstractCtrl.value;
    this.socialApi.searchUsers(term).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (page) => {
          this.searchResultsSubject.next(page.content);
          this.noResults = page.content.length == 0;
          setTimeout(() => this.searchReturnedSubject.next(SearchStatus.COMPLETE), 300);
        },
        error: (err) => {
          setTimeout(() => this.searchReturnedSubject.next(SearchStatus.COMPLETE), 300);
        }
      })
  }

  displayUser(user: PublicUser | null) : string {
    if(user) {
      return (user?.username + '#' + user?.userId);
    } else {
      return '';
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly SearchStatus = SearchStatus;
}
