import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { GroupListingsComponent } from './components/group-listings/group-listings.component';
import {provideHttpClient, withFetch} from "@angular/common/http";
import { GroupListingService } from "./services/group-listing.service";
import { UserComponent } from './components/user/user.component';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { FooterComponent } from './components/footer/footer.component';
import {NgOptimizedImage} from "@angular/common";
import { WelcomeScreenComponent } from './components/welcome-screen/welcome-screen.component';
import { CreateListingComponent } from './components/create-listing/create-listing.component';
import {ReactiveFormsModule} from "@angular/forms";

@NgModule({
  declarations: [
    AppComponent,
    GroupListingsComponent,
    UserComponent,
    NavBarComponent,
    FooterComponent,
    CreateListingComponent,
    WelcomeScreenComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgOptimizedImage,
    ReactiveFormsModule,
  ],
  providers: [
    provideClientHydration(), provideHttpClient(withFetch()), GroupListingService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
