import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgSelectModule} from "@ng-select/ng-select";
import {ServerDropdownComponent} from "../server-dropdown/server-dropdown.component";
import {EnvironmentDropdownComponent} from "../environment-dropdown/environment-dropdown.component";
import {ExperienceDropdownComponent} from "../experience-dropdown/experience-dropdown.component";
import {PlaystyleDropdownComponent} from "../playstyle-dropdown/playstyle-dropdown.component";
import {LegalityDropdownComponent} from "../legality-dropdown/legality-dropdown.component";
import {GroupStatusDropdownComponent} from "../group-status-dropdown/group-status-dropdown.component";
import {CategoryDropdownComponent} from "../category-dropdown/category-dropdown.component";
import {SubcategoryDropdownComponent} from "../subcategory-dropdown/subcategory-dropdown.component";
import {PvpStatusDropdownComponent} from "../pvp-status-dropdown/pvp-status-dropdown.component";
import {SystemDropdownComponent} from "../system-dropdown/system-dropdown.component";
import {PlanetDropdownComponent} from "../planet-dropdown/planet-dropdown.component";
import {CommsOptionDropdownComponent} from "../comms-option-dropdown/comms-option-dropdown.component";
import {CurrentPartySizeDropdownComponent} from "../current-party-size-dropdown/current-party-size-dropdown.component";
import {DesiredPartySizeDropdownComponent} from "../desired-party-size-dropdown/desired-party-size-dropdown.component";
import {ScheduleTimeDropdownComponent} from "../schedule-time-dropdown/schedule-time-dropdown.component";
import {ScheduleTimeZoneDropdownComponent} from "../schedule-time-zone-dropdown/schedule-time-zone-dropdown.component";
import {MatHint} from "@angular/material/form-field";

@NgModule({
  declarations: [
    ServerDropdownComponent,
    EnvironmentDropdownComponent,
    ExperienceDropdownComponent,
    PlaystyleDropdownComponent,
    LegalityDropdownComponent,
    GroupStatusDropdownComponent,
    CategoryDropdownComponent,
    SubcategoryDropdownComponent,
    SystemDropdownComponent,
    PlanetDropdownComponent,
    CommsOptionDropdownComponent,
    PvpStatusDropdownComponent,
    CurrentPartySizeDropdownComponent,
    DesiredPartySizeDropdownComponent,
    ScheduleTimeDropdownComponent,
    ScheduleTimeZoneDropdownComponent,
  ],
  imports: [
    CommonModule,
    NgSelectModule,
    FormsModule,
    ReactiveFormsModule,
    MatHint,
  ],
  exports: [
    ServerDropdownComponent,
    EnvironmentDropdownComponent,
    ExperienceDropdownComponent,
    PlaystyleDropdownComponent,
    LegalityDropdownComponent,
    GroupStatusDropdownComponent,
    CategoryDropdownComponent,
    SubcategoryDropdownComponent,
    SystemDropdownComponent,
    PlanetDropdownComponent,
    CommsOptionDropdownComponent,
    PvpStatusDropdownComponent,
    CurrentPartySizeDropdownComponent,
    DesiredPartySizeDropdownComponent,
    ScheduleTimeDropdownComponent,
    ScheduleTimeZoneDropdownComponent,
  ]
})
export class DropdownModule { }

