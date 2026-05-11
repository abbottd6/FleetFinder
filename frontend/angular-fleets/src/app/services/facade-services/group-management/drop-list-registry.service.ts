import { Injectable } from '@angular/core';
import {CdkDropList} from "@angular/cdk/drag-drop";

@Injectable({
  providedIn: 'root'
})
export class DropListRegistryService {
  private droppableLists: CdkDropList[] = [];

  constructor() { }

  registerList(list: CdkDropList) {
    this.droppableLists.push(list);
    this.refreshConnections();
  }

  unregisterList(unregister: CdkDropList) {
    const idx = this.droppableLists.indexOf(unregister);
    if(idx > -1) {
      this.droppableLists.splice(idx, 1);
    }
    this.refreshConnections();
  }

  getAll(): CdkDropList[] {
    return [...this.droppableLists];
  }

  private refreshConnections() {
    const sorted = this.droppableLists.sort((a, b) => {
      const aDepth = this.getDepth(a.element.nativeElement);
      const bDepth = this.getDepth(b.element.nativeElement);
      return bDepth - aDepth;
    });
    sorted.forEach(list => {
      list.connectedTo = sorted.filter(other => other !== list);
    })
  }

  private getDepth(el: HTMLElement): number {
    let depth = 0;
    let current = el;
    while (current.parentElement) {
      depth++;
      current = current.parentElement;
    }
    return depth;
  }
}
