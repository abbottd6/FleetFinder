import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-services/reference-data-api/lookup.service";
import {catchError, of, Subject} from "rxjs";
import {FormControl} from "@angular/forms";
import {environment} from "../../../../environments/environment";
import {PlayStyle} from "../../../models/reference-data/reference-data.models";

@Component({
  selector: 'app-playstyle-dropdown',
  standalone: false,

  templateUrl: './playstyle-dropdown.component.html',
  styleUrl: './playstyle-dropdown.component.css'
})
export class PlaystyleDropdownComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() playStyleControl!: FormControl;
  playStyles: PlayStyle[] = [];

  constructor(private lookupService: LookupService) {}

  ngOnInit(): void {
    this.fetchPlayStyles();
  }

  fetchPlayStyles(): void {
    this.lookupService.getPlayStyles()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown playstyles:', err);
          return of([]);
        })
      )
      .subscribe((data) => {this.playStyles = data;
        if(!environment.production) {
          console.log('Playstyles dropdown options fetched: ' + this.playStyles);
        }});
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
