import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-comms-option-dropdown',
  standalone: false,

  templateUrl: './comms-option-dropdown.component.html',
  styleUrl: './comms-option-dropdown.component.css'
})
export class CommsOptionDropdownComponent {
  @Input() commsOptionControl!: FormControl;

  commsOptions: { id: number; option: string }[] = [
    {id: 1, option: 'Required'},
    {id: 2, option: 'Optional'},
    {id: 3, option: 'No Comms'},
  ]
}
