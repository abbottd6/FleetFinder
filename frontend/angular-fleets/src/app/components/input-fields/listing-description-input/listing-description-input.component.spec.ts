import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ListingDescriptionInputComponent } from './listing-description-input.component';

describe('ListingDescriptionInputComponent', () => {
  let component: ListingDescriptionInputComponent;
  let fixture: ComponentFixture<ListingDescriptionInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListingDescriptionInputComponent],
      imports: [ReactiveFormsModule],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(ListingDescriptionInputComponent);
    component = fixture.componentInstance;
    component.descriptionControl = new FormControl('');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update characterCount to track value length', () => {
    component.descriptionControl.setValue('Hello World');
    component.updateCharacterCount();
    expect(component.characterCount).toBe(11);
  });

  it('should set characterCount to 0 for empty value', () => {
    component.descriptionControl.setValue('');
    component.updateCharacterCount();
    expect(component.characterCount).toBe(0);
  });
});
