import {Component, Input, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";

@Component({
  selector: 'app-comms-service-input',
  standalone: false,

  templateUrl: './comms-service-input.component.html',
  styleUrl: './comms-service-input.component.css'
})
export class CommsServiceInputComponent implements OnInit{
  @Input() parentForm!: FormGroup;
  characterCount: number = 0;

  updateCharacterCount() {
    const value = this.parentForm.controls['commsService'].value || '';
    this.characterCount = value.length;
  }

  ngOnInit() {

    //subscribing to comms option for enable/disable comms service input field
    this.parentForm.get('commsOption')?.valueChanges.subscribe(value => {

      //clearing comms service input if value changes to null or no comms
      if (value == null || value.option == 'No Comms') {
        this.parentForm.get('commsService')?.reset();
        this.parentForm.get('commsService')?.disable();
        this.characterCount = 0;
      }
      else {
        this.parentForm.get('commsService')?.enable();
      }
    })
  }
}
