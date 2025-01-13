import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ListingTitleInputComponent} from "../listing-title-input/listing-title-input.component";
import {ListingDescriptionInputComponent} from "../listing-description-input/listing-description-input.component";
import {AvailableRolesInputComponent} from "../available-roles-input/available-roles-input.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatError, MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatIconModule} from "@angular/material/icon";
import {CommsServiceInputComponent} from "../comms-service-input/comms-service-input.component";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";
import {
  EventScheduleDatepickerInputComponent
} from "../event-schedule-datepicker-input/event-schedule-datepicker-input.component";

@NgModule({
  declarations: [
    ListingTitleInputComponent,
    ListingDescriptionInputComponent,
    AvailableRolesInputComponent,
    CommsServiceInputComponent,
    EventScheduleDatepickerInputComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatError,
    MatDatepickerModule,
    MatNativeDateModule,
  ],
  exports: [
    ListingTitleInputComponent,
    ListingDescriptionInputComponent,
    AvailableRolesInputComponent,
    CommsServiceInputComponent,
    EventScheduleDatepickerInputComponent,
  ]
})
export class InputFieldModule { }
