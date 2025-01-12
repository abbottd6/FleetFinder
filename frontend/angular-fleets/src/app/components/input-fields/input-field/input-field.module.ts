import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ListingTitleInputComponent} from "../listing-title-input/listing-title-input.component";
import {ListingDescriptionInputComponent} from "../listing-description-input/listing-description-input.component";
import {AvailableRolesInputComponent} from "../available-roles-input/available-roles-input.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatError, MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatIconModule} from "@angular/material/icon";

@NgModule({
  declarations: [
    ListingTitleInputComponent,
    ListingDescriptionInputComponent,
    AvailableRolesInputComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatError
  ],
  exports: [
    ListingTitleInputComponent,
    ListingDescriptionInputComponent,
    AvailableRolesInputComponent,
  ]
})
export class InputFieldModule { }
