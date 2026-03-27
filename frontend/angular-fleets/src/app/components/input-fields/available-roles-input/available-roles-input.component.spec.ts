import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { AvailableRolesInputComponent } from './available-roles-input.component';

describe('AvailableRolesInputComponent', () => {
  let component: AvailableRolesInputComponent;
  let fixture: ComponentFixture<AvailableRolesInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AvailableRolesInputComponent],
      imports: [ReactiveFormsModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(AvailableRolesInputComponent);
    component = fixture.componentInstance;
    component.availableRolesControl = new FormControl('');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should bind the availableRolesControl input', () => {
    component.availableRolesControl.setValue('DPS, Healer');
    expect(component.availableRolesControl.value).toBe('DPS, Healer');
  });

  it('should update characterCount when value changes', () => {
    component.availableRolesControl.setValue('Tank');
    component.updateCharacterCount();
    expect(component.characterCount).toBe(4);
  });
});
