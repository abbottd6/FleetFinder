import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {CreateListingComponent} from "./create-listing.component";
import {DropdownModule} from "../dropdowns/dropdown-module/dropdown.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {InputFieldModule} from "../input-fields/input-field/input-field.module";
import {MatError} from "@angular/material/form-field";
import {RouterLink} from "@angular/router";
import {
    AbstractStringDropdownComponent
} from "../dropdowns/abstract-string-string-map-dropdown/abstract-string-dropdown.component";
import {
  GenericMediumInputFieldComponent
} from "../input-fields/generic-medium-input-field/generic-medium-input-field.component";
import {
  ListingJoinRequestPromptInputComponent
} from "../input-fields/listing-join-request-prompt-input/listing-join-request-prompt-input.component";
import {ListingDiscoveryTypeComponent} from "../dropdowns/listing-discovery-type/listing-discovery-type.component";

@NgModule({
  declarations: [
    CreateListingComponent,
  ],
  imports: [
    CommonModule,
    DropdownModule,
    InputFieldModule,
    FormsModule,
    ReactiveFormsModule,
    MatError,
    RouterLink,
    AbstractStringDropdownComponent,
    GenericMediumInputFieldComponent,
    ListingJoinRequestPromptInputComponent,
    ListingDiscoveryTypeComponent,
  ],
  exports:
   [CreateListingComponent]
})
export class CreateListingModule { }
