import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ConfirmReportComponent } from './confirm-report.component';

describe('ConfirmReportComponent', () => {
  let component: ConfirmReportComponent;
  let fixture: ComponentFixture<ConfirmReportComponent>;

  beforeEach(async () => {
    const dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      declarations: [ConfirmReportComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: { action: 'report', listing: {}, options: [] } },
        { provide: MatDialogRef, useValue: dialogRefSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('reportBasisCtrl starts null', () => {
    expect(component.reportBasisCtrl.value).toBeNull();
  });
});
