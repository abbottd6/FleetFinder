import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { throwError } from 'rxjs';
import { PlaystyleDropdownComponent } from './playstyle-dropdown.component';
import { LookupService } from '../../../services/api-services/reference-data-api/lookup.service';
import { PlayStyle } from '../../../models/reference-data/reference-data.models';

const mockPlayStyles: PlayStyle[] = [{ styleId: 1, playStyle: 'Casual' }];

describe('PlaystyleDropdownComponent', () => {
  let component: PlaystyleDropdownComponent;
  let fixture: ComponentFixture<PlaystyleDropdownComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PlaystyleDropdownComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, NgSelectModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(PlaystyleDropdownComponent);
    component = fixture.componentInstance;
    component.playStyleControl = new FormControl<number | null>(null);
    fixture.detectChanges();
    httpMock.expectOne(req => req.url.includes('/lookup/play-styles')).flush(mockPlayStyles);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch play styles on init and populate playStyles array', () => {
    expect(component.playStyles.length).toBe(1);
    expect(component.playStyles[0].playStyle).toBe('Casual');
  });

  it('should fall back to empty array on error', () => {
    const ls = TestBed.inject(LookupService);
    spyOn(ls, 'getPlayStyles').and.returnValue(throwError(() => new Error('err')));
    component.playStyles = [];
    component.fetchPlayStyles();
    expect(component.playStyles.length).toBe(0);
  });
});
