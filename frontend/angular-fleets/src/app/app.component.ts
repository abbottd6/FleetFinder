import {Component} from '@angular/core';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrl: './app.component.css',
    standalone: false
})
export class AppComponent {
  title = 'angular-fleets';

  constructor() {
    if('serviceWorker' in navigator) {
      navigator.serviceWorker.register('/service-worker.js');
    }
  }
}
