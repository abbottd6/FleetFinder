import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-services/reference-data-api/lookup.service";
import {catchError, of, pipe} from "rxjs";
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {environment} from "../../../../environments/environment";
import {PlayStyle} from "../../../models/reference-data/reference-data.models";

@Component({
  selector: 'app-playstyle-dropdown',
  standalone: false,

  templateUrl: './playstyle-dropdown.component.html',
  styleUrl: './playstyle-dropdown.component.css'
})
export class PlaystyleDropdownComponent implements AfterViewInit{
  @Input() playStyleControl!: FormControl;
  playStyles: PlayStyle[] = [];

  constructor(private lookupService: LookupService) {}

  ngAfterViewInit(): void {
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
}
