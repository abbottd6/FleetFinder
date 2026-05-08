import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnDestroy, ViewChild} from '@angular/core';
import {BreakpointObserver} from "@angular/cdk/layout";
import {map, Observable, shareReplay, Subject, takeUntil} from "rxjs";
import {LayoutMode} from "../input-fields/search-bar/search-bar.component";

@Component({
  selector: 'app-listing-success',
  standalone: false,
  templateUrl: './listing-success.component.html',
  styleUrl: './listing-success.component.css'
})
export class ListingSuccessComponent implements OnDestroy, AfterViewInit {
  private breakpointObserver = new BreakpointObserver();
  private destroy$ = new Subject<void>();

  @ViewChild('successPageContainer') successPageContainer!: ElementRef;
  protected containerHeight!: string;

  constructor(private cdr: ChangeDetectorRef){}

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

  ngAfterViewInit() {
    const top = this.successPageContainer.nativeElement.getBoundingClientRect().top;
    this.containerHeight = `calc(99vh - ${top}px)`;
    this.cdr.detectChanges();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
