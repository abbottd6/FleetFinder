import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-desired-party-size-dropdown',
  standalone: false,

  templateUrl: './desired-party-size-dropdown.component.html',
  styleUrl: './desired-party-size-dropdown.component.css'
})
export class DesiredPartySizeDropdownComponent implements AfterViewInit{
  @Input() desiredPartySizeControl!: FormControl;
  partySize: number[] = [];

  ngAfterViewInit() {
    this.partySize = Array.from({ length: 100 }, (_, i) => i + 1);
  }
}
