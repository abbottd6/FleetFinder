import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { of } from 'rxjs';

import { PlanetDropdownComponent } from './planet-dropdown.component';
import { LookupService } from '../../../services/api-services/reference-data-api/lookup.service';
import { PlanetMoonSystem } from '../../../models/reference-data/reference-data.models';

const mockPlanetMoons: PlanetMoonSystem[] = [
  { planetId: 1, planetName: 'Hurston',   systemId: 1, systemName: 'Stanton' },
  { planetId: 2, planetName: 'MicroTech', systemId: 1, systemName: 'Stanton' },
  { planetId: 3, planetName: 'Pyro I',    systemId: 2, systemName: 'Pyro' },
];

describe('PlanetDropdownComponent', () => {
  let component: PlanetDropdownComponent;
  let fixture: ComponentFixture<PlanetDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PlanetDropdownComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, NgSelectModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(PlanetDropdownComponent);
    component = fixture.componentInstance;
    component.planetarySystemControl = new FormControl<number | null>(null);
    component.planetMoonControl = new FormControl<number | null>({ value: null, disabled: true });

    // Spy on the real service instance before detectChanges triggers ngAfterViewInit
    const lookupService = TestBed.inject(LookupService);
    spyOn(lookupService, 'getPlanetMoonSystems').and.returnValue(of(mockPlanetMoons));

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch planet moon systems on init and populate planetMoonSystems array', () => {
    expect(component.planetMoonSystems.length).toBe(3);
    expect(component.planetMoonSystems[0].planetName).toBe('Hurston');
  });

  it('applyPlanetFilter() should filter planet moons by systemId', () => {
    component.applyPlanetFilter(1);
    expect(component.filteredPlanetMoons.length).toBe(2);
    expect(component.filteredPlanetMoons[0].planetName).toBe('Hurston');
    expect(component.filteredPlanetMoons[1].planetName).toBe('MicroTech');
  });

  it('applyPlanetFilter() should enable the control when matching planets exist', () => {
    component.applyPlanetFilter(1);
    expect(component.planetMoonControl.enabled).toBeTrue();
  });

  it('applyPlanetFilter() should filter to a single system correctly', () => {
    component.applyPlanetFilter(2);
    expect(component.filteredPlanetMoons.length).toBe(1);
    expect(component.filteredPlanetMoons[0].planetName).toBe('Pyro I');
  });

  it('applyPlanetFilter() should reset and disable the control when system is null', () => {
    component.applyPlanetFilter(null as any);
    expect(component.filteredPlanetMoons.length).toBe(0);
    expect(component.planetMoonControl.disabled).toBeTrue();
  });
});
