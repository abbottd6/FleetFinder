import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { throwError } from 'rxjs';
import { PvpStatusDropdownComponent } from './pvp-status-dropdown.component';
import { LookupService } from '../../../services/api-services/reference-data-api/lookup.service';
import { PvpStatus } from '../../../models/reference-data/reference-data.models';

const mockPvpStatuses: PvpStatus[] = [{ pvpStatusId: 1, pvpStatus: 'PvE' }];

describe('PvpStatusDropdownComponent', () => {
  let component: PvpStatusDropdownComponent;
  let fixture: ComponentFixture<PvpStatusDropdownComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PvpStatusDropdownComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, NgSelectModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(PvpStatusDropdownComponent);
    component = fixture.componentInstance;
    component.pvpStatusControl = new FormControl<number | null>(null);
    fixture.detectChanges();
    httpMock.expectOne(req => req.url.includes('/lookup/pvp-statuses')).flush(mockPvpStatuses);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch pvp statuses on init and populate pvpStatuses array', () => {
    expect(component.pvpStatuses.length).toBe(1);
    expect(component.pvpStatuses[0].pvpStatus).toBe('PvE');
  });

  it('should fall back to empty array on error', () => {
    const ls = TestBed.inject(LookupService);
    spyOn(ls, 'getPvpStatuses').and.returnValue(throwError(() => new Error('err')));
    component.pvpStatuses = [];
    component.fetchPvpStatuses();
    expect(component.pvpStatuses.length).toBe(0);
  });
});
