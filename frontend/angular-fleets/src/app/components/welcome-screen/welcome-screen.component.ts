import {AfterViewInit, Component} from '@angular/core';
import {AuthService} from "../../services/auth/auth-services/auth.service";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
    selector: 'app-welcome-screen',
    templateUrl: './welcome-screen.component.html',
    styleUrl: './welcome-screen.component.css',
    standalone: false
})
export class WelcomeScreenComponent implements AfterViewInit {
  constructor(public auth: AuthService, protected snackBar: MatSnackBar) {}

  ngAfterViewInit() {
    if(sessionStorage.getItem("post_logout_msg") === 'true') {
      this.snackBar.open("User logged out.", 'OK', {
        duration: 4000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['mobile-snackbar']
      })
    }
    sessionStorage.removeItem("post_logout_msg");
  }
}
