import {Component, Input} from '@angular/core';
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {MatError, MatFormField, MatHint, MatInput, MatLabel} from "@angular/material/input";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-generic-medium-input-field',
  standalone: true,
  templateUrl: './generic-medium-input-field.component.html',
  imports: [
    MatLabel,
    MatFormField,
    MatError,
    NgIf,
    MatInput,
    ReactiveFormsModule,
    MatHint
  ],
  styleUrl: './generic-medium-input-field.component.css'
})
export class GenericMediumInputFieldComponent {

  @Input() label: string | null = null;
  @Input() errorLabel: string | null = null;
  @Input() min: number | null = null;
  @Input() max: number | null = null;
  @Input() placeholder: string | null = null;
  @Input() inputCtrl!: FormControl;
  characterCount: number = 0;

  ngOnInit() {
    this.characterCount = this.inputCtrl.value.length || '';
  }

  updateCharacterCount() {
    const value = this.inputCtrl.value || '';
    this.characterCount = value.length;
  }
}
