import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-comms-service-input',
  standalone: false,

  templateUrl: './comms-service-input.component.html',
  styleUrl: './comms-service-input.component.css'
})
export class CommsServiceInputComponent implements OnInit{
  @Input() commsServiceControl!: FormControl;
  @Input() commsOptionControl!: FormControl;
  characterCount: number = 0;

  updateCharacterCount() {
    const value = this.commsServiceControl.value || '';
    this.characterCount = value.length;
  }

  ngOnInit() {

    //subscribing to comms option for enable/disable comms service input field
    this.commsOptionControl?.valueChanges.subscribe(value => {

      //clearing comms service input if value changes to null or no comms
      if (value == null || value.option == 'No Comms') {
        this.commsServiceControl?.reset();
        this.commsServiceControl?.disable();
        this.characterCount = 0;
      }
      else {
        this.commsServiceControl?.enable();
      }
    })
  }
}
