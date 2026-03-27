import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { DesiredPartySizeDropdownComponent } from './desired-party-size-dropdown.component';

describe('DesiredPartySizeDropdownComponent', () => {
  let component: DesiredPartySizeDropdownComponent;
  let fixture: ComponentFixture<DesiredPartySizeDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, NgSelectModule],
      declarations: [DesiredPartySizeDropdownComponent],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DesiredPartySizeDropdownComponent);
    component = fixture.componentInstance;
    component.desiredPartySizeControl = new FormControl(null);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should populate partySize with 100 entries starting from 2', () => {
    expect(component.partySize.length).toBe(100);
    expect(component.partySize[0]).toBe(2);
    expect(component.partySize[99]).toBe(101);
  });

  it('should accept a value via the desiredPartySizeControl input', () => {
    component.desiredPartySizeControl.setValue(10);
    expect(component.desiredPartySizeControl.value).toBe(10);
  });
});
