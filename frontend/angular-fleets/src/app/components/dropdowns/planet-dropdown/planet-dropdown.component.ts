import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-services/reference-data-api/lookup.service";
import {catchError, of} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";
import {environment} from "../../../../environments/environment";
import {PlanetMoonSystem} from "../../../models/reference-data/reference-data.models";

@Component({
  selector: 'app-planet-dropdown',
  standalone: false,

  templateUrl: './planet-dropdown.component.html',
  styleUrl: './planet-dropdown.component.css'
})
export class PlanetDropdownComponent implements AfterViewInit{
  @Input() planetMoonControl!: FormControl;
  @Input() planetarySystemControl!: FormControl;
  planetMoonSystems: PlanetMoonSystem[] = [];
  filteredPlanetMoons: PlanetMoonSystem[] = [];

  constructor(private lookupService: LookupService) { }

  ngAfterViewInit() {
    this.fetchPlanetMoonSystems();

    // Subscribing to planetary system changes to filter planet moons by system
    this.planetarySystemControl?.valueChanges.subscribe(value => {
      this.planetMoonControl?.reset();
      this.applyPlanetFilter(value);
    });
  }

  applyPlanetFilter(selectedSystem: number) {
    console.log("filtering planets for: ", selectedSystem);

    //filtering planet moons by selected value of planetarySystem dropdown
    //shows only planets that correspond to the selected system
    if (selectedSystem != null) {
      this.filteredPlanetMoons = this.planetMoonSystems.filter(
        planetMoon => planetMoon.systemId === selectedSystem
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
        const currentSystem = this.planetarySystemControl?.value;
        if (currentSystem != null) {
          this.applyPlanetFilter(currentSystem)
        }
        if(!environment.production) {
          console.log('Planet moon systems dropdown options fetched:', this.planetMoonSystems);
        }
      })
  }
}
