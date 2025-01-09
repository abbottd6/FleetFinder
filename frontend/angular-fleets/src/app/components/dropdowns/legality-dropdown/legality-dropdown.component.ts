import {Component, OnInit} from '@angular/core';
import {LookupService} from "../../../services/lookup.service";
import {catchError, of} from "rxjs";

@Component({
  selector: 'app-legality-dropdown',
  standalone: false,

  templateUrl: './legality-dropdown.component.html',
  styleUrl: './legality-dropdown.component.css'
})
export class LegalityDropdownComponent implements OnInit{
  legalities: {legalityId: number, legalityStatus: string}[] = [];

  constructor(private lookupService: LookupService) {}

  ngOnInit() {
      this.fetchLegalities();
      console.log('Legality dropdown options fetched:' + this.legalities);
  }

  fetchLegalities() {
    this.lookupService.getLegalities()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown legality options:', err);
          return of([])
        })
      )
      .subscribe((data) => {this.legalities = data;});
  }
}
