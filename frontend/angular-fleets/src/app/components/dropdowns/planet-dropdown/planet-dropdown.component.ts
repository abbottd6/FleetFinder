import {Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/lookup.service";
import {catchError, of} from "rxjs";
import {FormGroup} from "@angular/forms";

@Component({
  selector: 'app-planet-dropdown',
  standalone: false,

  templateUrl: './planet-dropdown.component.html',
  styleUrl: './planet-dropdown.component.css'
})
export class PlanetDropdownComponent implements OnInit{
  @Input() parentForm!: FormGroup;
  planetMoonSystems: {planetId: number, planetName: string, systemName: string}[] = [];

  constructor(private lookupService: LookupService) { }

  ngOnInit() {
    this.fetchPlanetMoonSystems();
  }

  fetchPlanetMoonSystems(): void {
    this.lookupService.getPlanetMoonSystems()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown planet moon systems:', err);
          return of([]);
        })
      )
      .subscribe((data) => {this.planetMoonSystems = data;
      console.log('Planet moon systems dropdown options fetched:', this.planetMoonSystems);})
  }
}
