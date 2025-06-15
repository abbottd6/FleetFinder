import {Component, inject, OnInit} from '@angular/core';
import {AuthenticatedResult, OidcSecurityService} from "angular-auth-oidc-client";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrl: './app.component.css',
    standalone: false
})
export class AppComponent {
  title = 'angular-fleets';
}
