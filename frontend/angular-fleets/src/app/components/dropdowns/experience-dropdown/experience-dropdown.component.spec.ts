import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { throwError } from 'rxjs';
import { ExperienceDropdownComponent } from './experience-dropdown.component';
import { LookupService } from '../../../services/api-services/reference-data-api/lookup.service';
import { GameExperience } from '../../../models/reference-data/reference-data.models';

const mockExperiences: GameExperience[] = [{ experienceId: 1, experienceType: 'Veteran' }];

describe('ExperienceDropdownComponent', () => {
  let component: ExperienceDropdownComponent;
  let fixture: ComponentFixture<ExperienceDropdownComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ExperienceDropdownComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, NgSelectModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(ExperienceDropdownComponent);
    component = fixture.componentInstance;
    component.experienceControl = new FormControl<number | null>(null);
    fixture.detectChanges();
    httpMock.expectOne(req => req.url.includes('/lookup/game-experiences')).flush(mockExperiences);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch game experiences on init and populate experiences array', () => {
    expect(component.experiences.length).toBe(1);
    expect(component.experiences[0].experienceType).toBe('Veteran');
  });

  it('should fall back to empty array on error', () => {
    const ls = TestBed.inject(LookupService);
    spyOn(ls, 'getGameExperiences').and.returnValue(throwError(() => new Error('err')));
    component.experiences = [];
    component.fetchGameExperiences();
    expect(component.experiences.length).toBe(0);
  });
});
