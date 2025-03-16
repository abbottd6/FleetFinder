import {Component, Input} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-listing-title-input',
  standalone: false,

  templateUrl: './listing-title-input.component.html',
  styleUrl: './listing-title-input.component.css'
})
export class ListingTitleInputComponent {
  @Input() titleControl!: FormControl;
  characterCount: number = 0;

  updateCharacterCount() {
    const value = this.titleControl.value || '';
    this.characterCount = value.length;
  }
}
