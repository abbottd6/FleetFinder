import {Component, Input} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";


@Component({
  selector: 'app-listing-description-input',
  standalone: false,

  templateUrl: './listing-description-input.component.html',
  styleUrl: './listing-description-input.component.css'
})
export class ListingDescriptionInputComponent {
  @Input() descriptionControl!: FormControl;
  characterCount: number = 0;

  updateCharacterCount() {
    const value = this.descriptionControl.value || '';
    this.characterCount = value.length;
  }
}
