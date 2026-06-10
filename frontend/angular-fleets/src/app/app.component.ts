import {Component, DestroyRef, ElementRef, inject, ViewChild} from '@angular/core';
import {NavigationEnd, Router} from "@angular/router";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {filter} from "rxjs";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrl: './app.component.css',
    standalone: false
})
export class AppComponent {
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  title = 'angular-fleets';

  @ViewChild('contentContainer') private contentContainer!: ElementRef<HTMLElement>;

  constructor() {
    if('serviceWorker' in navigator) {
      navigator.serviceWorker.register('/service-worker.js');
    }

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => this.contentContainer?.nativeElement.scrollTo({ top: 0 }));
  }
}
