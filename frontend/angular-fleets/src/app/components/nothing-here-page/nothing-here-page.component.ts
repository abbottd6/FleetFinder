import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnDestroy, ViewChild} from '@angular/core';
import {BreakpointObserver} from "@angular/cdk/layout";
import {map, Observable, shareReplay, Subject} from "rxjs";
import {LayoutMode} from "../input-fields/search-bar/search-bar.component";
import {RouterLink} from "@angular/router";
import {AsyncPipe, NgIf, NgOptimizedImage} from "@angular/common";

@Component({
  selector: 'app-nothing-here-page',
  standalone: true,
  templateUrl: './nothing-here-page.component.html',
  imports: [
    RouterLink,
    NgIf,
    AsyncPipe,
    NgOptimizedImage
  ],
  styleUrl: './nothing-here-page.component.css'
})
export class NothingHerePageComponent implements AfterViewInit, OnDestroy {
  private breakpointObserver = new BreakpointObserver();
  private destroy$ = new Subject<void>();

  @ViewChild('nothingHereContainer') nothingHereContainer!: ElementRef;
  protected containerHeight!: string;

  constructor(private cdr: ChangeDetectorRef){}

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

  ngAfterViewInit() {
    const top = this.nothingHereContainer.nativeElement.getBoundingClientRect().top;
    this.containerHeight = `calc(99vh - ${top}px)`;
    this.cdr.detectChanges();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
