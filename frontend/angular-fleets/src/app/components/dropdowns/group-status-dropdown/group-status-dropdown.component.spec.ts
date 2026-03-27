import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { throwError } from 'rxjs';
import { GroupStatusDropdownComponent } from './group-status-dropdown.component';
import { LookupService } from '../../../services/api-services/reference-data-api/lookup.service';
import { GroupStatus } from '../../../models/reference-data/reference-data.models';

const mockGroupStatuses: GroupStatus[] = [{ groupStatusId: 1, groupStatus: 'Open' }];

describe('GroupStatusDropdownComponent', () => {
  let component: GroupStatusDropdownComponent;
  let fixture: ComponentFixture<GroupStatusDropdownComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GroupStatusDropdownComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, NgSelectModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(GroupStatusDropdownComponent);
    component = fixture.componentInstance;
    component.groupStatusControl = new FormControl<number | null>(null);
    fixture.detectChanges();
    httpMock.expectOne(req => req.url.includes('/lookup/group-statuses')).flush(mockGroupStatuses);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch group statuses on init and populate groupStatuses array', () => {
    expect(component.groupStatuses.length).toBe(1);
    expect(component.groupStatuses[0].groupStatus).toBe('Open');
  });

  it('should fall back to empty array on error', () => {
    const ls = TestBed.inject(LookupService);
    spyOn(ls, 'getGroupStatuses').and.returnValue(throwError(() => new Error('err')));
    component.groupStatuses = [];
    component.fetchGroupStatuses();
    expect(component.groupStatuses.length).toBe(0);
  });
});
