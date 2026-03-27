import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-services/reference-data-api/lookup.service";
import {catchError, of, Subject} from "rxjs";
import {FormControl} from "@angular/forms";
import {environment} from "../../../../environments/environment";
import {Legality} from "../../../models/reference-data/reference-data.models";

@Component({
  selector: 'app-legality-dropdown',
  standalone: false,

  templateUrl: './legality-dropdown.component.html',
  styleUrl: './legality-dropdown.component.css'
})
export class LegalityDropdownComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() legalityControl!: FormControl;
  legalities: Legality[] = [];

  constructor(private lookupService: LookupService) {}

  ngOnInit() {
      this.fetchLegalities();
  }

  fetchLegalities() {
    this.lookupService.getLegalities()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown legality options:', err);
          return of([])
        })
      )
      .subscribe((data) => {this.legalities = data;
        if(!environment.production) {
          console.log('Legality dropdown options fetched:' + this.legalities);
        }});
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
