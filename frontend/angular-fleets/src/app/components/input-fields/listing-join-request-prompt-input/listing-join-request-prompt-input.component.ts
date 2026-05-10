import {Component, Input} from '@angular/core';
import {MatFormField, MatHint, MatInput, MatLabel} from "@angular/material/input";
import {FormControl, ReactiveFormsModule} from "@angular/forms";

@Component({
  selector: 'app-listing-join-request-prompt-input',
    imports: [
        MatFormField,
        MatHint,
        MatInput,
        MatLabel,
        ReactiveFormsModule
    ],
  templateUrl: './listing-join-request-prompt-input.component.html',
  styleUrl: './listing-join-request-prompt-input.component.css'
})
export class ListingJoinRequestPromptInputComponent {
  @Input() joinRequestPromptControl!: FormControl;
  characterCount: number = 0;

  updateCharacterCount() {
    const value = this.joinRequestPromptControl.value || '';
    this.characterCount = value.length;
  }
}
