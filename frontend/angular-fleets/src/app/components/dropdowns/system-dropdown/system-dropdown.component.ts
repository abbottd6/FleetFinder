import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-lookup-services/lookup.service";
import {catchError, of} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-system-dropdown',
  standalone: false,

  templateUrl: './system-dropdown.component.html',
  styleUrl: './system-dropdown.component.css'
})
export class SystemDropdownComponent implements AfterViewInit{
  @Input() planetarySystemControl!: FormControl;
  systems: {systemId: number, systemName: string}[] = [];

  constructor(private lookupService: LookupService) { }

  ngAfterViewInit() {
    this.lookupService.getPlanetarySystems()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown planet moon systems:', err);
          return of([]);
        })
      )
      .subscribe((data) => {this.systems = data;
      console.log('Planetary systems dropdown options fetched:', this.systems);
      });
  }
}
