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
    ],
  exports:
   [CreateListingComponent]
})
export class CreateListingModule { }
