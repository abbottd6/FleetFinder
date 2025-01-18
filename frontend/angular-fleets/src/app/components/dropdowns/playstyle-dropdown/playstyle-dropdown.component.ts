import {Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-lookup-services/lookup.service";
import {catchError, of, pipe} from "rxjs";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-playstyle-dropdown',
  standalone: false,

  templateUrl: './playstyle-dropdown.component.html',
  styleUrl: './playstyle-dropdown.component.css'
})
export class PlaystyleDropdownComponent implements OnInit{
  @Input() parentForm!: FormGroup;
  playStyles: {styleId: number, playStyle: string}[] = [];

  constructor(private lookupService: LookupService) {}

  ngOnInit(): void {
    this.fetchPlayStyles();
    console.log('Play styles dropdown options fetched: ' + this.playStyles);
  }

  fetchPlayStyles(): void {
    this.lookupService.getPlayStyles()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown playstyles:', err);
          return of([]);
        })
      )
      .subscribe((data) => {this.playStyles = data;});
  }
}
