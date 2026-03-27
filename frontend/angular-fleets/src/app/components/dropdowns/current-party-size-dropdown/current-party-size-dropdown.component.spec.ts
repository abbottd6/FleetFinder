import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgSelectModule } from '@ng-select/ng-select';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { CurrentPartySizeDropdownComponent } from './current-party-size-dropdown.component';

describe('CurrentPartySizeDropdownComponent', () => {
  let component: CurrentPartySizeDropdownComponent;
  let fixture: ComponentFixture<CurrentPartySizeDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, NgSelectModule],
      declarations: [CurrentPartySizeDropdownComponent],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CurrentPartySizeDropdownComponent);
    component = fixture.componentInstance;
    component.currentPartySizeControl = new FormControl(null);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should populate partySize with 100 entries starting from 1', () => {
    expect(component.partySize.length).toBe(100);
    expect(component.partySize[0]).toBe(1);
    expect(component.partySize[99]).toBe(100);
  });

  it('should accept a value via the currentPartySizeControl input', () => {
    component.currentPartySizeControl.setValue(5);
    expect(component.currentPartySizeControl.value).toBe(5);
  });
});
