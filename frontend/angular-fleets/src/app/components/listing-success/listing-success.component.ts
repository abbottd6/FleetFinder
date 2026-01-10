import {AfterViewInit, Component, OnDestroy} from '@angular/core';
import {BreakpointObserver} from "@angular/cdk/layout";
import {map, Observable, shareReplay, Subject, takeUntil} from "rxjs";
import {LayoutMode} from "../input-fields/search-bar/search-bar.component";

@Component({
  selector: 'app-listing-success',
  standalone: false,
  templateUrl: './listing-success.component.html',
  styleUrl: './listing-success.component.css'
})
export class ListingSuccessComponent implements OnDestroy {
  private breakpointObserver = new BreakpointObserver();
  private destroy$ = new Subject<void>();

  successLayoutMode$: Observable<LayoutMode> = this.breakpointObserver
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
