import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-services/reference-data-api/lookup.service";
import {catchError, of, Subject} from "rxjs";
import {FormControl} from "@angular/forms";
import {environment} from "../../../../environments/environment";
import {GameEnvironment} from "../../../models/reference-data/reference-data.models";

@Component({
  selector: 'app-environment-dropdown',
  standalone: false,

  templateUrl: './environment-dropdown.component.html',
  styleUrl: './environment-dropdown.component.css'
})
export class EnvironmentDropdownComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  @Input() environmentControl!: FormControl;
  environments: GameEnvironment[] = [];


  constructor(private lookupService: LookupService) {}

  ngOnInit() {
    this.fetchGameEnvironments();
  }

  fetchGameEnvironments(): void {
    this.lookupService.getGameEnvironments()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown game environments:', err);
          return of([]);
        })
      )
      .subscribe((data) => {this.environments = data;
        if(!environment.production) {
          console.log('Environments dropdown options fetched:', this.environments);
        }
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
