import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subject} from "rxjs";
import {
  GroupManagementMemberViewModel
} from "../../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {DatePipe} from "@angular/common";

@Component({
  selector: 'app-member-chip',
  standalone: true,
  templateUrl: './member-chip.component.html',
  imports: [
    DatePipe
  ],
  styleUrl: './member-chip.component.css'
})
export class MemberChipComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() member!: GroupManagementMemberViewModel;

  ngOnInit() {

  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
