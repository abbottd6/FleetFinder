import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-services/reference-data-api/lookup.service";
import {catchError, of, Subject} from "rxjs";
import {FormControl} from "@angular/forms";
import {environment} from "../../../../environments/environment";
import {GroupStatus} from "../../../models/reference-data/reference-data.models";

@Component({
  selector: 'app-group-status-dropdown',
  standalone: false,

  templateUrl: './group-status-dropdown.component.html',
  styleUrl: './group-status-dropdown.component.css'
})
export class GroupStatusDropdownComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() groupStatusControl!: FormControl;
  groupStatuses: GroupStatus[] = [];

  constructor(private lookupService: LookupService) {}

  ngOnInit(): void {
    this.fetchGroupStatuses()
  }

  fetchGroupStatuses() {
    this.lookupService.getGroupStatuses()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown group statuses:', err);
          return of([]);
        })
      )
      .subscribe((data) => {this.groupStatuses = data;
        if(!environment.production) {
          console.log('Group status dropdown options fetched:', this.groupStatuses);
        }
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
