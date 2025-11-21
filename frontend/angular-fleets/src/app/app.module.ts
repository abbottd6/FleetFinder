import { NgModule } from '@angular/core';
import {BrowserModule, provideClientHydration} from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { GroupListingsComponent } from './components/group-listings/group-listings.component';
import {HTTP_INTERCEPTORS, provideHttpClient, withFetch, withInterceptorsFromDi} from "@angular/common/http";
import { GroupListingFetchService } from "./services/group-listing-services/group-listing-fetch.service";
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { FooterComponent } from './components/footer/footer.component';
import { NgOptimizedImage } from "@angular/common";
import { WelcomeScreenComponent } from './components/welcome-screen/welcome-screen.component';
import { CreateListingModule } from "./components/create-listing/create-listing.module";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {
    MAT_FORM_FIELD_DEFAULT_OPTIONS,
    MatError,
    MatFormField,
    MatHint,
    MatLabel,
    MatSuffix
} from "@angular/material/form-field";
import {NgSelectComponent} from "@ng-select/ng-select";
import { GroupListingModalComponent } from './components/group-listing-modal/group-listing-modal.component';
import { AboutComponent } from './components/about/about.component';
import {AuthModule} from "angular-auth-oidc-client";
import {environment} from "../environments/environment";
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {AuthInterceptor} from "./services/auth/interceptors/auth.interceptor";
import {MatSidenav, MatSidenavContainer} from "@angular/material/sidenav";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { LoginModalComponent } from './components/login-modal/login-modal.component';
import {MatSnackBarModule} from "@angular/material/snack-bar";
import { UpdateListingComponent } from './components/update-listing/update-listing.component';
import {DropdownModule} from "./components/dropdowns/dropdown-module/dropdown.module";
import {InputFieldModule} from "./components/input-fields/input-field/input-field.module";
import {MatButtonModule} from "@angular/material/button";
import { ModListingsTableComponent } from './components/mod-listings-table/mod-listings-table.component';
import {
  MatCell,
  MatCellDef,
  MatColumnDef, MatFooterCell, MatFooterCellDef, MatFooterRow, MatFooterRowDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable
} from "@angular/material/table";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatMenu, MatMenuTrigger} from "@angular/material/menu";
import {MatIcon} from "@angular/material/icon";
import {TooltipPosition, MatTooltipModule} from "@angular/material/tooltip";
import {MatSort, MatSortHeader} from "@angular/material/sort";
import {MatPaginator} from "@angular/material/paginator";
import { SearchBarComponent } from './components/input-fields/search-bar/search-bar.component';
import {MatInput} from "@angular/material/input";
import { ConfirmDeleteComponent } from './components/pop-ups/confirm-delete/confirm-delete.component';
import {MatDialogActions, MatDialogContent, MatDialogTitle} from "@angular/material/dialog";


@NgModule({
  declarations: [
    AppComponent,
    GroupListingsComponent,
    NavBarComponent,
    FooterComponent,
    WelcomeScreenComponent,
    AboutComponent,
    LoginModalComponent,
    SearchBarComponent,
    ConfirmDeleteComponent,
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
        MatButtonModule,
        MatSidenav,
        MatSnackBarModule,
        DropdownModule,
        InputFieldModule,
        MatError,
        GroupListingModalComponent,
        ModListingsTableComponent,
        MatCell,
        MatCellDef,
        MatCheckbox,
        MatColumnDef,
        MatHeaderCell,
        MatHeaderRow,
        MatHeaderRowDef,
        MatRow,
        MatRowDef,
        MatTable,
        MatHeaderCellDef,
        MatMenuTrigger,
        MatIcon,
        MatMenu,
        MatTooltipModule,
        MatSortHeader,
        MatSort,
        MatFooterRowDef,
        MatFooterRow,
        MatFooterCellDef,
        MatFooterCell,
        MatPaginator,
        MatFormField,
        MatHint,
        MatInput,
        MatLabel,
        MatDialogContent,
        MatDialogTitle,
        MatDialogActions,
        MatSuffix,
    ],
  providers: [
    provideClientHydration(),
    provideHttpClient(withFetch(), withInterceptorsFromDi()),
    GroupListingFetchService,
    provideAnimationsAsync(),
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true},
    {provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {appearance: 'outline'}},

  ],
  exports: [
    ModListingsTableComponent
  ],
  bootstrap: [AppComponent]
})

export class AppModule { }
