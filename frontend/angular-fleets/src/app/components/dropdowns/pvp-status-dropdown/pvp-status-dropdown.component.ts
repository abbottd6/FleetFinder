import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-services/reference-data-api/lookup.service";
import {catchError, of, Subject} from "rxjs";
import {FormControl} from "@angular/forms";
import {environment} from "../../../../environments/environment";
import {PvpStatus} from "../../../models/reference-data/reference-data.models";

@Component({
  selector: 'app-pvp-status-dropdown',
  standalone: false,

  templateUrl: './pvp-status-dropdown.component.html',
  styleUrl: './pvp-status-dropdown.component.css'
})

export class PvpStatusDropdownComponent implements OnInit, OnDestroy {
  private destroy$: Subject<void> = new Subject<void>();

  @Input() pvpStatusControl!: FormControl;
  pvpStatuses: PvpStatus[] = [];

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
        if(!environment.production) {
          console.log('Pvp status dropdown options fetched:', this.pvpStatuses);
        }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
