import { NgModule } from '@angular/core';
import {BrowserModule, provideClientHydration} from '@angular/platform-browser';

import { OAuthModule, OAuthService, AuthConfig } from 'angular-oauth2-oidc';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { GroupListingsComponent } from './components/group-listings/group-listings.component';
import {provideHttpClient, withFetch, withInterceptors} from "@angular/common/http";
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
import {NgSelectComponent} from "@ng-select/ng-select";
import { GroupListingModalComponent } from './components/group-listing-modal/group-listing-modal.component';
import { AboutComponent } from './components/about/about.component';
import {includeBearerTokenInterceptor, provideKeycloak} from "keycloak-angular";

export const authConfig: AuthConfig = {
  issuer: 'http://localhost:8180/realms/oauthrealm',
  redirectUri: window.location.origin + '/login/callback',
  clientId: 'fleetfinder-frontend',
  responseType: 'code',
  scope: 'openid profile email',
  showDebugInformation: true,
  strictDiscoveryDocumentValidation: false
}

@NgModule({
  declarations: [
    AppComponent,
    GroupListingsComponent,
    UserComponent,
    NavBarComponent,
    FooterComponent,
    WelcomeScreenComponent,
    GroupListingModalComponent,
    AboutComponent,
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
    provideClientHydration(),
    provideHttpClient(withFetch(), withInterceptors([ includeBearerTokenInterceptor ])),
    GroupListingFetchService,
    provideAnimationsAsync(),
    {provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {appearance: 'outline'}},
    provideKeycloak({
      config: {
        url: 'http://localhost:8180',
        realm: 'oauthrealm',
        clientId: 'fleetfinder-frontend',
      },
      initOptions: {
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri:
          window.location.origin + '/assets/silent-check-sso.html'
      }
    })
  ],
  exports: [],
  bootstrap: [AppComponent]
})

export class AppModule { }
