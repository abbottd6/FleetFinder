import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-current-party-size-dropdown',
  standalone: false,

  templateUrl: './current-party-size-dropdown.component.html',
  styleUrl: './current-party-size-dropdown.component.css'
})
export class CurrentPartySizeDropdownComponent implements AfterViewInit {
  @Input() currentPartySizeControl!: FormControl;
  partySize: number[] = [];

  ngAfterViewInit() {
    this.partySize = Array.from({ length: 100 }, (_, i) => i + 1);
  }
}
