import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { NEVER } from 'rxjs';
import { Router } from '@angular/router';

import { ListingTemplateModalComponent } from './listing-template-modal.component';
import { TemplatesModalService } from '../../../services/component-services/templates-modal-service/templates-modal.service';
import { ListingTemplatesApiService } from '../../../services/api-services/listing-templates-api/listing-templates-api.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';

describe('ListingTemplateModalComponent', () => {
  let component: ListingTemplateModalComponent;
  let fixture: ComponentFixture<ListingTemplateModalComponent>;

  beforeEach(async () => {
    const templatesModalSpy = jasmine.createSpyObj('TemplatesModalService', ['closeModal'], {
      close: { subscribe: () => {} },
      refresh$: NEVER
    });
    const templatesApiSpy = jasmine.createSpyObj('ListingTemplatesApiService', ['getTemplates', 'createTemplate']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    const dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [ListingTemplateModalComponent],
      providers: [
        { provide: TemplatesModalService, useValue: templatesModalSpy },
        { provide: ListingTemplatesApiService, useValue: templatesApiSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(ListingTemplateModalComponent);
    component = fixture.componentInstance;
    component.templateModalIsVisible = false;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
