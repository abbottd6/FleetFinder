import {Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-lookup-services/lookup.service";
import {catchError, of} from "rxjs";
import {FormGroup} from "@angular/forms";

@Component({
  selector: 'app-pvp-status-dropdown',
  standalone: false,

  templateUrl: './pvp-status-dropdown.component.html',
  styleUrl: './pvp-status-dropdown.component.css'
})

export class PvpStatusDropdownComponent implements OnInit{
  @Input() parentForm!: FormGroup;
  pvpStatuses: {pvpStatusId: number, pvpStatus: string}[] = [];

  constructor(private lookupService: LookupService) { }

  ngOnInit(): void {
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
        console.log('Pvp status dropdown options fetched:', this.pvpStatuses);
  }
}
