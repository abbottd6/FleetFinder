import {DestroyRef, inject, Injectable} from '@angular/core';
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {fromEvent} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class MouseEventService {
  private destroyRef = inject(DestroyRef);

  readonly mouseUp$ = fromEvent<MouseEvent>(document, 'mouseup').pipe(
    takeUntilDestroyed(this.destroyRef)
  );

  constructor() {}
}
