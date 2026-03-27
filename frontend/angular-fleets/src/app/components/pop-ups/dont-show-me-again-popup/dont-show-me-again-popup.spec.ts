import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DontShowMeAgainPopup } from './dont-show-me-again-popup';

describe('DontShowMeAgainPopup', () => {
  let component: DontShowMeAgainPopup;
  let fixture: ComponentFixture<DontShowMeAgainPopup>;

  beforeEach(async () => {
    const dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      declarations: [DontShowMeAgainPopup],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: { message: 'test message' } },
        { provide: MatDialogRef, useValue: dialogRefSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(DontShowMeAgainPopup);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('dontShowMe starts false', () => {
    expect(component.dontShowMe.value).toBeFalse();
  });
});
