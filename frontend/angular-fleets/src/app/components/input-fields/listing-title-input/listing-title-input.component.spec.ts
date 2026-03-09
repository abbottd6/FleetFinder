import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ListingTitleInputComponent } from './listing-title-input.component';

describe('ListingTitleInputComponent', () => {
  let component: ListingTitleInputComponent;
  let fixture: ComponentFixture<ListingTitleInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListingTitleInputComponent],
      imports: [ReactiveFormsModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(ListingTitleInputComponent);
    component = fixture.componentInstance;
    component.titleControl = new FormControl('');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update characterCount when value changes', () => {
    component.titleControl.setValue('Hello');
    component.updateCharacterCount();
    expect(component.characterCount).toBe(5);
  });

  it('should set characterCount to 0 for empty value', () => {
    component.titleControl.setValue('');
    component.updateCharacterCount();
    expect(component.characterCount).toBe(0);
  });
});
