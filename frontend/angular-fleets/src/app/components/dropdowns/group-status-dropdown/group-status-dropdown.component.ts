import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-lookup-services/lookup.service";
import {catchError, of} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-group-status-dropdown',
  standalone: false,

  templateUrl: './group-status-dropdown.component.html',
  styleUrl: './group-status-dropdown.component.css'
})
export class GroupStatusDropdownComponent implements AfterViewInit{
  @Input() groupStatusControl!: FormControl;
  groupStatuses: {groupStatusId: number, groupStatus: string}[] = [];

  constructor(private lookupService: LookupService) {}

  ngAfterViewInit(): void {
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
}
