import {Component, Input} from '@angular/core';
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {MatError} from "@angular/material/input";
import {NgIf} from "@angular/common";
import {NgSelectComponent} from "@ng-select/ng-select";

export interface DiscoveryOptions {
  displayVal: string,
  systemVal: string,
}

@Component({
  selector: 'app-listing-discovery-type',
  imports: [
    MatError,
    NgIf,
    NgSelectComponent,
    ReactiveFormsModule
  ],
  templateUrl: './listing-discovery-type.component.html',
  styleUrl: './listing-discovery-type.component.css'
})
export class ListingDiscoveryTypeComponent {
  @Input() discoveryControl!: FormControl;

  protected discoveryOptions: DiscoveryOptions[] = [
    { displayVal: 'Public', systemVal: 'PUBLIC' },
    { displayVal: 'Shared Link Only', systemVal: 'PRIVATE_LINK' },
    //{ displayVal: 'Select Channels', systemVal: 'CHANNELS' }
  ]
}
