import {Component, OnInit} from '@angular/core';
import {UserService} from "../../services/user-services/user.service";
import {AuthService} from "../../services/auth/auth-services/auth.service";
import {Observable} from "rxjs";
import {PrivateUser} from "../../models/private-user/private-user";
import {Router, RouterModule} from "@angular/router";
import {CommonModule} from "@angular/common";
import {NgSelectComponent} from "@ng-select/ng-select";
import {MatSidenav, MatSidenavModule} from "@angular/material/sidenav";

@Component({
    selector: 'app-user',
    templateUrl: './user.component.html',
    styleUrls: [
      './user.component.css',
      '../create-listing/create-listing.component.css'
    ],
    imports: [ CommonModule , RouterModule, MatSidenavModule],
    standalone: true
})
export class UserComponent {
  localUser$: Observable<PrivateUser>;

  constructor(private userService: UserService, private authService: AuthService, private router: Router) {
    this.localUser$ = this.authService.localUser$;
  }
}
