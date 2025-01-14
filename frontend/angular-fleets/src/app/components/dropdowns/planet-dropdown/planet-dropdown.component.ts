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
  planetMoonSystems: {planetId: number, planetName: string, systemName: number}[] = [];
  filteredPlanetMoons: {planetId: number, planetName: string, systemName: number}[] = [];

  constructor(private lookupService: LookupService) { }

  ngOnInit() {
    this.fetchPlanetMoonSystems();

    // Subscribing to planetary system changes to filter planet moons by system
    this.parentForm.get('planetarySystem')?.valueChanges.subscribe(value => {

      //clearing filtered planet array after value change
      this.parentForm.get('planetMoon')?.reset();
      this.parentForm.get('planetName')?.disable();
      this.filteredPlanetMoons.splice(0, this.filteredPlanetMoons.length);

      //filtering planet moons by selected value of planetarySystem dropdown
      //shows only planets that correspond to the selected system
      if (value != null && value.systemName != 'Any' ) {
        this.filteredPlanetMoons = this.planetMoonSystems.filter(
          planetMoon => planetMoon.systemName === value.systemName
        );
        if (this.filteredPlanetMoons.length > 0) {
          this.parentForm.get('planetMoon')?.enable();
        }
        else {
          this.parentForm.get('planetMoon')?.reset();
          this.parentForm.get('planetMoon')?.disable();
        }
      }
      else {
        this.parentForm.get('planetMoon')?.reset();
        this.parentForm.get('planetMoon')?.disable();
      }
      console.log('Filtered Planet Moons: ', this.filteredPlanetMoons);
    })
  }

  //Fetching planet moon systems from API
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
