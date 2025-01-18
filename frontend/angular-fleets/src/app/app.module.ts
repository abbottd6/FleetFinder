import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { GroupListingsComponent } from './components/group-listings/group-listings.component';
import { provideHttpClient, withFetch } from "@angular/common/http";
import { GroupListingFetchService } from "./services/group-listing-services/group-listing-fetch.service";
import { UserComponent } from './components/user/user.component';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { FooterComponent } from './components/footer/footer.component';
import { NgOptimizedImage } from "@angular/common";
import { WelcomeScreenComponent } from './components/welcome-screen/welcome-screen.component';
import { CreateListingModule } from "./components/create-listing/create-listing.module";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {MAT_FORM_FIELD_DEFAULT_OPTIONS} from "@angular/material/form-field";
import { CommsServiceInputComponent } from './components/input-fields/comms-service-input/comms-service-input.component';
import { CurrentPartySizeDropdownComponent } from './components/dropdowns/current-party-size-dropdown/current-party-size-dropdown.component';
import { DesiredPartySizeDropdownComponent } from './components/dropdowns/desired-party-size-dropdown/desired-party-size-dropdown.component';
import {NgSelectComponent} from "@ng-select/ng-select";
import { EventScheduleDatepickerInputComponent } from './components/input-fields/event-schedule-datepicker-input/event-schedule-datepicker-input.component';


@NgModule({
  declarations: [
    AppComponent,
    GroupListingsComponent,
    UserComponent,
    NavBarComponent,
    FooterComponent,
    WelcomeScreenComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgOptimizedImage,
    CreateListingModule,
    FormsModule,
    ReactiveFormsModule,
    NgSelectComponent,
  ],
  providers: [
    provideClientHydration(), provideHttpClient(withFetch()), GroupListingFetchService, provideAnimationsAsync(),
    {provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {appearance: 'outline'}}
  ],
  exports: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
