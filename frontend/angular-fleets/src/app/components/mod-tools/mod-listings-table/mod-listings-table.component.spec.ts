import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';

import { ModListingsTableComponent } from './mod-listings-table.component';
import { ModApiService } from '../../../services/api-services/mod-api/mod-api.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

describe('ModListingsTableComponent', () => {
  let component: ModListingsTableComponent;
  let fixture: ComponentFixture<ModListingsTableComponent>;

  beforeEach(async () => {
    const modApiSpy = jasmine.createSpyObj('ModApiService', ['modGetGroupListings', 'refreshModPanel']);
    modApiSpy.modGetGroupListings.and.returnValue(of([]));
    const routerSpy = jasmine.createSpyObj('Router', ['navigate', 'navigateByUrl']);
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [ModListingsTableComponent],
      providers: [
        { provide: ModApiService, useValue: modApiSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatSnackBar, useValue: snackBarSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(ModListingsTableComponent);
    component = fixture.componentInstance;
    component.modGroupListings = [];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('listingForModal EventEmitter should be defined', () => {
    expect(component.listingForModal).toBeDefined();
  });
});
