import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { throwError } from 'rxjs';
import { EnvironmentDropdownComponent } from './environment-dropdown.component';
import { LookupService } from '../../../services/api-services/reference-data-api/lookup.service';
import { GameEnvironment } from '../../../models/reference-data/reference-data.models';

const mockEnvironments: GameEnvironment[] = [{ environmentId: 1, environmentType: 'PU' }];

describe('EnvironmentDropdownComponent', () => {
  let component: EnvironmentDropdownComponent;
  let fixture: ComponentFixture<EnvironmentDropdownComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EnvironmentDropdownComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, NgSelectModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(EnvironmentDropdownComponent);
    component = fixture.componentInstance;
    component.environmentControl = new FormControl<number | null>(null);
    fixture.detectChanges();
    httpMock.expectOne(req => req.url.includes('/lookup/game-environments')).flush(mockEnvironments);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch game environments on init and populate environments array', () => {
    expect(component.environments.length).toBe(1);
    expect(component.environments[0].environmentType).toBe('PU');
  });

  it('should fall back to empty array on error', () => {
    const ls = TestBed.inject(LookupService);
    spyOn(ls, 'getGameEnvironments').and.returnValue(throwError(() => new Error('err')));
    component.environments = [];
    component.fetchGameEnvironments();
    expect(component.environments.length).toBe(0);
  });
});
