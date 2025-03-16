import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-available-roles-input',
  standalone: false,

  templateUrl: './available-roles-input.component.html',
  styleUrl: './available-roles-input.component.css'
})
export class AvailableRolesInputComponent {
  @Input() availableRolesControl!: FormControl;
  characterCount: number = 0;

  updateCharacterCount() {
    const value = this.availableRolesControl.value || '';
    this.characterCount = value.length;
  }
}
