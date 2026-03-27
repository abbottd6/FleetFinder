import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-services/reference-data-api/lookup.service";
import {catchError, of, Subject} from "rxjs";
import {FormControl} from "@angular/forms";
import {environment} from "../../../../environments/environment";
import {PlanetarySystem} from "../../../models/reference-data/reference-data.models";

@Component({
  selector: 'app-system-dropdown',
  standalone: false,

  templateUrl: './system-dropdown.component.html',
  styleUrl: './system-dropdown.component.css'
})
export class SystemDropdownComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() planetarySystemControl!: FormControl;
  systems: PlanetarySystem[] = [];

  constructor(private lookupService: LookupService) { }

  ngOnInit() {
    this.lookupService.getPlanetarySystems()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown planet moon systems:', err);
          return of([]);
        })
      )
      .subscribe((data) => {this.systems = data;
        if(!environment.production) {
          console.log('Planetary systems dropdown options fetched:', this.systems);
        }
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
