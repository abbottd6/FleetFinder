import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-lookup-services/lookup.service";
import {catchError, of, pipe} from "rxjs";
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-playstyle-dropdown',
  standalone: false,

  templateUrl: './playstyle-dropdown.component.html',
  styleUrl: './playstyle-dropdown.component.css'
})
export class PlaystyleDropdownComponent implements AfterViewInit{
  @Input() playStyleControl!: FormControl;
  playStyles: {styleId: number, playStyle: string}[] = [];

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
