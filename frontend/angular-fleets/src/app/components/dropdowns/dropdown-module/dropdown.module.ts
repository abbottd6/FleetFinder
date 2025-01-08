import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgSelectModule} from "@ng-select/ng-select";
import {ServerDropdownComponent} from "../server-dropdown/server-dropdown.component";

@NgModule({
  declarations: [
    ServerDropdownComponent
  ],
  imports: [
    CommonModule,
    NgSelectModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  exports: [
    ServerDropdownComponent
  ]
})
export class DropdownModule { }

