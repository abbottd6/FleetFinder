import {AfterViewInit, Directive, ElementRef, OnDestroy} from "@angular/core";

@Directive ({
  selector: '[measureFooterHeight]',
  standalone: true
})

export class MeasureNavbarHeightDirective implements AfterViewInit, OnDestroy {
  private resizeObserver?: ResizeObserver;

  constructor(private el: ElementRef<HTMLElement>) {}

  ngAfterViewInit(): void {
    const footer = this.el.nativeElement;

    const setVar = () => {
      const height = footer.offsetHeight ?? 0;
      document.documentElement.style.setProperty('--footer-height', `${height}px`)
    };

    setVar();

    this.resizeObserver = new ResizeObserver(() => setVar());
    this.resizeObserver.observe(footer);

    window.addEventListener('load', setVar, { once: true });
  }

  ngOnDestroy(): void {
    this.resizeObserver?.disconnect();
  }
}
