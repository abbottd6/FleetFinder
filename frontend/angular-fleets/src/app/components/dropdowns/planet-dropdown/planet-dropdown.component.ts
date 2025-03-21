import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-lookup-services/lookup.service";
import {catchError, of} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-planet-dropdown',
  standalone: false,

  templateUrl: './planet-dropdown.component.html',
  styleUrl: './planet-dropdown.component.css'
})
export class PlanetDropdownComponent implements AfterViewInit{
  @Input() planetMoonControl!: FormControl;
  @Input() planetarySystemControl!: FormControl;
  planetMoonSystems: {planetId: number, planetName: string, systemName: number}[] = [];
  filteredPlanetMoons: {planetId: number, planetName: string, systemName: number}[] = [];

  constructor(private lookupService: LookupService) { }

  ngAfterViewInit() {
    this.fetchPlanetMoonSystems();

    // Subscribing to planetary system changes to filter planet moons by system
    this.planetarySystemControl?.valueChanges.subscribe(value => {

      //clearing filtered planet array after value change and resetting dropdown
      this.planetMoonControl?.reset();
      this.planetMoonControl?.disable();
      this.filteredPlanetMoons.splice(0, this.filteredPlanetMoons.length);

      //filtering planet moons by selected value of planetarySystem dropdown
      //shows only planets that correspond to the selected system
      if (value != null && value.systemName != 'Any') {
        this.filteredPlanetMoons = this.planetMoonSystems.filter(
          planetMoon => planetMoon.systemName === value.systemName
        );
        if (this.filteredPlanetMoons.length > 0) {
          this.planetMoonControl?.enable();
        }
        else {
          this.planetMoonControl?.reset();
          this.planetMoonControl?.disable();
        }
      }
      else {
        this.planetMoonControl?.reset();
        this.planetMoonControl?.disable();
      }
      if(!environment.production) {
        console.log('Filtered Planet Moons: ', this.filteredPlanetMoons);
      }
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
        if(!environment.production) {
          console.log('Planet moon systems dropdown options fetched:', this.planetMoonSystems);
        }
      })
  }
}
