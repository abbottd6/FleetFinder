import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { throwError } from 'rxjs';
import { LegalityDropdownComponent } from './legality-dropdown.component';
import { LookupService } from '../../../services/api-services/reference-data-api/lookup.service';
import { Legality } from '../../../models/reference-data/reference-data.models';

const mockLegalities: Legality[] = [{ legalityId: 1, legalityStatus: 'Lawful' }];

describe('LegalityDropdownComponent', () => {
  let component: LegalityDropdownComponent;
  let fixture: ComponentFixture<LegalityDropdownComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LegalityDropdownComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, NgSelectModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(LegalityDropdownComponent);
    component = fixture.componentInstance;
    component.legalityControl = new FormControl<number | null>(null);
    fixture.detectChanges();
    httpMock.expectOne(req => req.url.includes('/lookup/legalities')).flush(mockLegalities);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch legalities on init and populate legalities array', () => {
    expect(component.legalities.length).toBe(1);
    expect(component.legalities[0].legalityStatus).toBe('Lawful');
  });

  it('should fall back to empty array on error', () => {
    const ls = TestBed.inject(LookupService);
    spyOn(ls, 'getLegalities').and.returnValue(throwError(() => new Error('err')));
    component.legalities = [];
    component.fetchLegalities();
    expect(component.legalities.length).toBe(0);
  });
});
