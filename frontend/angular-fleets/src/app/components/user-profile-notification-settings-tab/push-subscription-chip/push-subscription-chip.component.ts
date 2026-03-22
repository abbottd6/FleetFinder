import {Component, Input, OnDestroy} from '@angular/core';
import { Subject } from 'rxjs';
import {PushSubViewModel} from "../../../models/NotificationPrefAndCustomNotesModels/PushSubViewModel";

@Component({
  selector: 'app-push-subscription-chip',
  standalone: true,
  templateUrl: './push-subscription-chip.component.html',
  styleUrl: './push-subscription-chip.component.css'
})
export class PushSubscriptionChipComponent implements OnDestroy {
  private destroy$ = new Subject<void>
  @Input() inputSub!: PushSubViewModel;

  constructor(){
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
