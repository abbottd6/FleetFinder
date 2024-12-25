import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { GroupListingsTableComponent } from './components/group-listings-table/group-listings-table.component';
import { provideHttpClient } from "@angular/common/http";
import { GroupListingService } from "./services/group-listing.service";
import { UserComponent } from './components/user/user.component';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { FooterComponent } from './components/footer/footer.component';
import {NgOptimizedImage} from "@angular/common";
import { WelcomeScreenComponent } from './components/welcome-screen/welcome-screen.component';

@NgModule({
  declarations: [
    AppComponent,
    GroupListingsTableComponent,
    UserComponent,
    NavBarComponent,
    FooterComponent,
    WelcomeScreenComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgOptimizedImage,
  ],
  providers: [
    provideClientHydration(), provideHttpClient(), GroupListingService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
