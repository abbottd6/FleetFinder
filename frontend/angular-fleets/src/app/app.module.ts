import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { GroupListingsComponent } from './components/group-listings/group-listings.component';
import {HttpClientModule, provideHttpClient} from "@angular/common/http";
import {GroupListingService} from "./services/group-listing.service";

@NgModule({
  declarations: [
    AppComponent,
    GroupListingsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
  ],
  providers: [
    provideClientHydration(), provideHttpClient(), GroupListingService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
