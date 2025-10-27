import { NgModule } from '@angular/core';
import {BrowserModule, provideClientHydration} from '@angular/platform-browser';

import { OAuthModule, OAuthService, AuthConfig } from 'angular-oauth2-oidc';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { GroupListingsComponent } from './components/group-listings/group-listings.component';
import {HTTP_INTERCEPTORS, provideHttpClient, withFetch, withInterceptorsFromDi} from "@angular/common/http";
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
import {AuthModule} from "angular-auth-oidc-client";
import {environment} from "../environments/environment";
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {AuthInterceptor} from "./services/auth/interceptors/auth.interceptor";
import {MatSidenav, MatSidenavContainer} from "@angular/material/sidenav";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { UserAcctListingsTableComponent } from './components/user-acct-listings-table/user-acct-listings-table.component';
import { LoginModalComponent } from './components/login-modal/login-modal.component';


@NgModule({
  declarations: [
    AppComponent,
    GroupListingsComponent,
    NavBarComponent,
    FooterComponent,
    WelcomeScreenComponent,
    GroupListingModalComponent,
    AboutComponent,
    LoginModalComponent,
  ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutingModule,
        NgOptimizedImage,
        CreateListingModule,
        FormsModule,
        ReactiveFormsModule,
        NgSelectComponent,
        AuthModule.forRoot({
            config: {
                ...environment.oidc
            }
        }),
        NgbModule,
        MatSidenavContainer,
        MatSidenav
    ],
  providers: [
    provideClientHydration(),
    provideHttpClient(withFetch(), withInterceptorsFromDi()),
    GroupListingFetchService,
    provideAnimationsAsync(),
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true},
    {provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {appearance: 'outline'}},

  ],
  exports: [],
  bootstrap: [AppComponent]
})

export class AppModule { }
