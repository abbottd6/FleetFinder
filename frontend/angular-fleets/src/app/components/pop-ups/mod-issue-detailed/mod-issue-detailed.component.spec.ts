import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ModIssueDetailedComponent } from './mod-issue-detailed.component';

describe('ModIssueDetailedComponent', () => {
  let component: ModIssueDetailedComponent;
  let fixture: ComponentFixture<ModIssueDetailedComponent>;

  beforeEach(async () => {
    const dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      declarations: [ModIssueDetailedComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: { issue: {}, listing: {} } },
        { provide: MatDialogRef, useValue: dialogRefSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(ModIssueDetailedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('data.issue is accessible', () => {
    expect(component.data.issue).toBeDefined();
  });
});
