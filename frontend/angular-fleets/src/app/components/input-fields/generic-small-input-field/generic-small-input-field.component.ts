import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {NgIf} from "@angular/common";
import {MatError, MatHint} from "@angular/material/form-field";

@Component({
  selector: 'app-generic-small-input-field',
  standalone: true,
  templateUrl: './generic-small-input-field.component.html',
  imports: [
    FormsModule,
    MatError,
    MatFormField,
    MatHint,
    MatInput,
    MatLabel,
    NgIf,
    MatFormField,
    ReactiveFormsModule
  ],
  styleUrl: './generic-small-input-field.component.css'
})
export class GenericSmallInputFieldComponent implements OnInit {

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
