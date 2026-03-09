import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { throwError } from 'rxjs';
import { ServerDropdownComponent } from './server-dropdown.component';
import { LookupService } from '../../../services/api-services/reference-data-api/lookup.service';
import { ListingFormService } from '../../../services/listing-form-service/listing-form.service';
import { ServerRegion } from '../../../models/reference-data/reference-data.models';

const mockServers: ServerRegion[] = [{ serverId: 1, servername: 'USA' }];

describe('ServerDropdownComponent', () => {
  let component: ServerDropdownComponent;
  let fixture: ComponentFixture<ServerDropdownComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ServerDropdownComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule, NgSelectModule],
      providers: [
        { provide: ListingFormService, useValue: { serverRegion: new FormControl(null) } }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(ServerDropdownComponent);
    component = fixture.componentInstance;
    component.serverControl = new FormControl<number | null>(null);
    fixture.detectChanges();
    httpMock.expectOne(req => req.url.includes('/lookup/server-regions')).flush(mockServers);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch server regions on init and populate servers array', () => {
    expect(component.servers.length).toBe(1);
    expect(component.servers[0].servername).toBe('USA');
  });

  it('should fall back to empty array on error', () => {
    const ls = TestBed.inject(LookupService);
    spyOn(ls, 'getServerRegions').and.returnValue(throwError(() => new Error('err')));
    component.servers = [];
    component.fetchServerRegions();
    expect(component.servers.length).toBe(0);
  });

  it('should return correct server id for a known region name', () => {
    component.servers = [{ serverId: 2, servername: 'EU' }];
    expect(component.findServerByName('EU')).toBe(2);
  });

  it('should return null for an unknown region name', () => {
    expect(component.findServerByName('Mars')).toBeNull();
  });
});
