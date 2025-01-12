import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {CreateListingComponent} from "./create-listing.component";
import {DropdownModule} from "../dropdowns/dropdown-module/dropdown.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {InputFieldModule} from "../input-fields/input-field/input-field.module";



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
  ],
  exports:
   [CreateListingComponent]
})
export class CreateListingModule { }
