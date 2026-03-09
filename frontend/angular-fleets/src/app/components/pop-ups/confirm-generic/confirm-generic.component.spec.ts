import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ConfirmGenericComponent } from './confirm-generic.component';

describe('ConfirmGenericComponent', () => {
  let component: ConfirmGenericComponent;
  let fixture: ComponentFixture<ConfirmGenericComponent>;

  beforeEach(async () => {
    const dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      declarations: [ConfirmGenericComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: { message: 'Are you sure?', title: 'Confirm' } },
        { provide: MatDialogRef, useValue: dialogRefSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmGenericComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('data.message === "Are you sure?"', () => {
    expect(component.data.message).toBe('Are you sure?');
  });
});
