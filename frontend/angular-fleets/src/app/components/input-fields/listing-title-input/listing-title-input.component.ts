import {Component, Input} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-listing-title-input',
  standalone: false,

  templateUrl: './listing-title-input.component.html',
  styleUrl: './listing-title-input.component.css'
})
export class ListingTitleInputComponent {
  @Input() parentForm!: FormGroup;
  characterCount: number = 0;

  updateCharacterCount() {
    const value = this.parentForm.controls['listingTitle'].value || '';
    this.characterCount = value.length;
  }

  get listingTitle(): FormControl {return this.parentForm.controls['titleGroup.listingTitle'] as FormControl};

  get isListingTitleValid(): boolean {
    const control = this.parentForm.controls['listingTitle'];
    return control?.invalid && (control?.dirty || control?.touched);
  }
}
