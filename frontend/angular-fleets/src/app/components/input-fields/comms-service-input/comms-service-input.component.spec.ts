import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { CommsServiceInputComponent } from './comms-service-input.component';

describe('CommsServiceInputComponent', () => {
  let component: CommsServiceInputComponent;
  let fixture: ComponentFixture<CommsServiceInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CommsServiceInputComponent],
      imports: [ReactiveFormsModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(CommsServiceInputComponent);
    component = fixture.componentInstance;
    component.commsServiceControl = new FormControl('');
    component.commsOptionControl = new FormControl('');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should bind the commsServiceControl input', () => {
    component.commsServiceControl.setValue('Discord');
    expect(component.commsServiceControl.value).toBe('Discord');
  });

  it('should update characterCount when value changes', () => {
    component.commsServiceControl.setValue('Discord');
    component.updateCharacterCount();
    expect(component.characterCount).toBe(7);
  });
});
