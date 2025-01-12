import {Component, Input} from '@angular/core';
import {FormGroup} from "@angular/forms";

@Component({
  selector: 'app-listing-description-input',
  standalone: false,

  templateUrl: './listing-description-input.component.html',
  styleUrl: './listing-description-input.component.css'
})
export class ListingDescriptionInputComponent {
  @Input() parentForm!: FormGroup;
  characterCount: number = 0;

  updateCharacterCount() {
    const value = this.parentForm.controls['listingDescription'].value || '';
    this.characterCount = value.length;
  }
}
