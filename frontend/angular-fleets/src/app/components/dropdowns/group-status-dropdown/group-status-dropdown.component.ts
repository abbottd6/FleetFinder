import {Component, OnInit} from '@angular/core';
import {LookupService} from "../../../services/lookup.service";
import {catchError, of} from "rxjs";

@Component({
  selector: 'app-group-status-dropdown',
  standalone: false,

  templateUrl: './group-status-dropdown.component.html',
  styleUrl: './group-status-dropdown.component.css'
})
export class GroupStatusDropdownComponent implements OnInit{
  groupStatuses: {groupStatusId: number, groupStatus: string}[] = [];

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
      console.log('Gameplay subcategories dropdown options fetched:', this.groupStatuses);
      });
  }
}
