import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { SystemDropdownComponent } from './system-dropdown.component';
import { PlanetarySystem } from '../../../models/reference-data/reference-data.models';

const mockSystems: PlanetarySystem[] = [{ systemId: 1, systemName: 'Stanton' }];

describe('SystemDropdownComponent', () => {
  let component: SystemDropdownComponent;
  let fixture: ComponentFixture<SystemDropdownComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SystemDropdownComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, NgSelectModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(SystemDropdownComponent);
    component = fixture.componentInstance;
    component.planetarySystemControl = new FormControl<number | null>(null);
    fixture.detectChanges();
    httpMock.expectOne(req => req.url.includes('/lookup/planetary-systems')).flush(mockSystems);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch planetary systems on init and populate systems array', () => {
    expect(component.systems.length).toBe(1);
    expect(component.systems[0].systemName).toBe('Stanton');
  });
});
