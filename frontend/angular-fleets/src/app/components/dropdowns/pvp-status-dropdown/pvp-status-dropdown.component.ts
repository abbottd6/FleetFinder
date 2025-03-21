import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-lookup-services/lookup.service";
import {catchError, of} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-pvp-status-dropdown',
  standalone: false,

  templateUrl: './pvp-status-dropdown.component.html',
  styleUrl: './pvp-status-dropdown.component.css'
})

export class PvpStatusDropdownComponent implements AfterViewInit{
  @Input() pvpStatusControl!: FormControl;
  pvpStatuses: {pvpStatusId: number, pvpStatus: string}[] = [];

  constructor(private lookupService: LookupService) { }

  ngAfterViewInit(): void {
    this.fetchPvpStatuses();
  }

  fetchPvpStatuses() {
    this.lookupService.getPvpStatuses()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown pvp statuses:', err);
          return of([]);
        })
      )
      .subscribe((data) => {this.pvpStatuses = data;})
        if(!environment.production) {
          console.log('Pvp status dropdown options fetched:', this.pvpStatuses);
        }
  }
}
