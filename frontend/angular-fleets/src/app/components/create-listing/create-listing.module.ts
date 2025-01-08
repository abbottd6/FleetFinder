import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {CreateListingComponent} from "./create-listing.component";
import {DropdownModule} from "../dropdowns/dropdown-module/dropdown.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";



@NgModule({
  declarations: [
    CreateListingComponent,
  ],
  imports: [
    CommonModule,
    DropdownModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  exports:
   [CreateListingComponent]
})
export class CreateListingModule { }
