import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subject} from "rxjs";
import {NotificationViewModel} from "../../../models/NotificationViewModel";

@Component({
  selector: 'app-notification-chip-with-metadata',
  standalone: true,
  templateUrl: './notification-chip-with-metadata.component.html',
  styleUrl: './notification-chip-with-metadata.component.css'
})
export class NotificationChipWithMetadataComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>()

  @Input() inputNote!: NotificationViewModel;

  ngOnInit() {

  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
