import {EventEmitter, Injectable, Input, Output} from '@angular/core';
import {CloseValue} from "../../../components/group-listing-modal/group-listing-modal.component";
import {fromEvent, Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class TemplatesModalService {
  templateModalIsVisible: boolean = false;
  close = new EventEmitter<CloseValue>
  private refreshSubject = new Subject<'hide' | 'bookmark' | 'unbookmark' | 'report' | 'delete' | null>();
  readonly refresh$ = this.refreshSubject.asObservable();
  private templateModalCloseFromPop = false;

  constructor() {
    fromEvent<PopStateEvent>(window, 'popstate').subscribe(() => {
      if(this.templateModalIsVisible) {
        this.templateModalCloseFromPop = true;
        this.closeModal(null);

        this.templateModalCloseFromPop = false;
      }
    });
  }

  private emitTemplatesRefresh(reason: 'delete') {
    this.refreshSubject.next(reason);
  }

  closeModal(action: CloseValue['value']) {
    this.templateModalIsVisible = false;
    const emitVal: CloseValue = {
      value: action,
      group: null,
    }

    if (action === 'delete') {
      this.emitTemplatesRefresh(action);
    }

    this.close.emit(emitVal);
  }
}
