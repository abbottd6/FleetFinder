import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subject} from "rxjs";
import {
  GroupManagementMemberViewModel
} from "../../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {DatePipe, NgIf, SlicePipe} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {toTitleCase} from "../../../../utils/global-functions";

@Component({
  selector: 'app-member-chip',
  standalone: true,
  templateUrl: './member-chip.component.html',
  imports: [
    NgIf,
    MatIcon,
    MatTooltip,
    SlicePipe,
    DatePipe
  ],
  styleUrl: './member-chip.component.css'
})
export class MemberChipComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  protected detailsExpanded: boolean = true;

  @Input() member!: GroupManagementMemberViewModel;

  ngOnInit() {

  }

  toggleDetailsExpand() {
    this.detailsExpanded = !this.detailsExpanded;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly toTitleCase = toTitleCase;
}
