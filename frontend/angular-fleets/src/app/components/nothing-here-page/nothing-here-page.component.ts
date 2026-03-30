import {Component, OnDestroy} from '@angular/core';
import {BreakpointObserver} from "@angular/cdk/layout";
import {map, Observable, shareReplay, Subject} from "rxjs";
import {LayoutMode} from "../input-fields/search-bar/search-bar.component";
import {RouterLink} from "@angular/router";
import {AsyncPipe, NgIf} from "@angular/common";

@Component({
  selector: 'app-nothing-here-page',
  standalone: true,
  templateUrl: './nothing-here-page.component.html',
  imports: [
    RouterLink,
    NgIf,
    AsyncPipe
  ],
  styleUrl: './nothing-here-page.component.css'
})
export class NothingHerePageComponent implements OnDestroy {
  private breakpointObserver = new BreakpointObserver();
  private destroy$ = new Subject<void>();

  nothingHereLayoutMode$: Observable<LayoutMode> = this.breakpointObserver
    .observe([
      '(max-width: 900px)',
      '(min-width: 901px)'
    ])
    .pipe(
      map(state => {
        if (state.breakpoints['(max-width: 900px)']) {
          return 'handheld';
        } else {
          return 'full';
        }
      }),
      shareReplay(1)
    );

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
