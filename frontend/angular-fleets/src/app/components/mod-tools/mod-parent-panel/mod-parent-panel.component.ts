import {Component, EventEmitter, OnDestroy, Output} from '@angular/core';
import {MatTabsModule} from "@angular/material/tabs";
import {Subject} from "rxjs";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {ModIssuesViewComponent} from "../mod-issues-view/mod-issues-view.component";
import {ModActionsTableComponent} from "../mod-actions-table/mod-actions-table.component";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-mod-parent-panel',
  standalone: true,
  templateUrl: './mod-parent-panel.component.html',
  styleUrl: './mod-parent-panel.component.css',
  imports: [MatTabsModule, ModIssuesViewComponent, ModActionsTableComponent],
})
export class ModParentPanelComponent implements OnDestroy {
  private modParentPanelDestroy$: Subject<void> = new Subject<void>();
  @Output() listingForModal = new EventEmitter<GroupListingViewModel>();

  constructor(){}

  emitChildClick(listing: GroupListingViewModel) {
    if(!environment.production) {
      console.log("listing emitted: ", listing.listingTitle);
    }
    this.listingForModal.emit(listing);
  }

  ngOnDestroy() {
    this.modParentPanelDestroy$.next();
    this.modParentPanelDestroy$.complete();
  }
}
