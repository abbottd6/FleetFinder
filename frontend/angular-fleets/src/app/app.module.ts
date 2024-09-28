import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { GroupListingsTableComponent } from './components/group-listings-table/group-listings-table.component';
import { provideHttpClient } from "@angular/common/http";
import { GroupListingService } from "./services/group-listing.service";
import { UserComponent } from './components/user/user.component';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';

@NgModule({
  declarations: [
    AppComponent,
    GroupListingsTableComponent,
    UserComponent,
    NavBarComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
  ],
  providers: [
    provideClientHydration(), provideHttpClient(), GroupListingService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
