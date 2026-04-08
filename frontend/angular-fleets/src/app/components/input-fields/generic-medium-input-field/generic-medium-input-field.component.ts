import {Component, Input} from '@angular/core';
import {FormControl} from "@angular/forms";

@Component({
  selector: 'app-generic-medium-input-field',
  standalone: false,
  templateUrl: './generic-medium-input-field.component.html',
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
    this.characterCount = this.inputCtrl.value.length;
  }

  updateCharacterCount() {
    const value = this.inputCtrl.value || '';
    this.characterCount = value.length;
  }
}
