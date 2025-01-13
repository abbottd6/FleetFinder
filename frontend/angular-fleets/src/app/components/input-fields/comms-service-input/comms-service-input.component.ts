import {Component, Input} from '@angular/core';
import {FormGroup} from "@angular/forms";

@Component({
  selector: 'app-comms-service-input',
  standalone: false,

  templateUrl: './comms-service-input.component.html',
  styleUrl: './comms-service-input.component.css'
})
export class CommsServiceInputComponent {
  @Input() parentForm!: FormGroup;
  characterCount: number = 0;

  updateCharacterCount() {
    const value = this.parentForm.controls['commsService'].value || '';
    this.characterCount = value.length;
  }
}
