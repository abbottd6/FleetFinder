import { NgModule } from '@angular/core';
import {BrowserModule, provideClientHydration} from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { GroupListingsComponent } from './components/group-listings/group-listings.component';
import {HTTP_INTERCEPTORS, provideHttpClient, withFetch, withInterceptorsFromDi} from "@angular/common/http";
import { GroupListingFetchService } from "./services/api-services/group-listings-fetch-api/group-listing-fetch.service";
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
import {DropdownModule} from "./components/dropdowns/dropdown-module/dropdown.module";
import {InputFieldModule} from "./components/input-fields/input-field/input-field.module";
import {MatButtonModule} from "@angular/material/button";
import { ModListingsTableComponent } from './components/mod-tools/mod-listings-table/mod-listings-table.component';
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
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatIcon} from "@angular/material/icon";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatSort, MatSortHeader} from "@angular/material/sort";
import {MatPaginator} from "@angular/material/paginator";
import { SearchBarComponent } from './components/input-fields/search-bar/search-bar.component';
import {MatInput} from "@angular/material/input";
import { ConfirmDeleteComponent } from './components/pop-ups/confirm-delete/confirm-delete.component';
import {MatDialogActions, MatDialogContent, MatDialogTitle} from "@angular/material/dialog";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerToggle,
  MatDateRangeInput, MatDateRangePicker,
  MatEndDate, MatStartDate
} from "@angular/material/datepicker";
import { ConfirmReportComponent } from './components/pop-ups/confirm-report/confirm-report.component';
import { MobileFiltersPopupComponent } from './components/pop-ups/mobile-filters-popup/mobile-filters-popup.component';
import { FilterDropdownsComponent } from './components/dropdowns/filter-dropdowns/filter-dropdowns.component';
import { DontShowMeAgainPopup } from './components/pop-ups/dont-show-me-again-popup/dont-show-me-again-popup';
import {MatBadge} from "@angular/material/badge";
import { ConfirmGenericComponent } from './components/pop-ups/confirm-generic/confirm-generic.component';
import { ConfirmClearIssueComponent } from './components/pop-ups/confirm-clear-issue/confirm-clear-issue.component';
import { ModIssueDetailedComponent } from './components/pop-ups/mod-issue-detailed/mod-issue-detailed.component';
import {ChatShellComponent} from "./components/chat/shell-component/chat-shell.component";
import {MeasureFooterHeightDirective} from "./utils/measure-footer-height-directive";
import { ChatPanelComponent } from './components/chat/chat-panel/chat-panel.component';
import {NavBarComponent} from "./components/nav-bar/nav-bar.component";
import {
  NotificationsDropdownComponent
} from "./components/dropdowns/notifications/notifications-dropdown/notifications-dropdown.component";
import {NotificationComponent} from "./components/dropdowns/notifications/notification/notification.component";
import { ListingSuccessComponent } from './components/listing-success/listing-success.component';
import { HowToComponent } from './components/how-to/how-to.component';
import { UserDeleteAccountPopupComponent } from './components/pop-ups/user-delete-account-popup/user-delete-account-popup.component';
import { ConfirmDelinkDiscordPopupComponent } from './components/pop-ups/confirm-delink-discord-popup/confirm-delink-discord-popup.component';

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
    ConfirmReportComponent,
    MobileFiltersPopupComponent,
    FilterDropdownsComponent,
    DontShowMeAgainPopup,
    ConfirmGenericComponent,
    ConfirmClearIssueComponent,
    ModIssueDetailedComponent,
    ListingSuccessComponent,
    HowToComponent,
    UserDeleteAccountPopupComponent,
    ConfirmDelinkDiscordPopupComponent,
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
    MatChipSet,
    MatChip,
    MatDatepicker,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatDateRangeInput,
    MatEndDate,
    MatDateRangePicker,
    MatStartDate,
    MatMenuItem,
    MatBadge,
    ChatShellComponent,
    ChatPanelComponent,
    MeasureFooterHeightDirective,
    NotificationsDropdownComponent,
    NotificationComponent,
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
    ModListingsTableComponent,
  ],
  bootstrap: [AppComponent]
})

export class AppModule { }
