import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-lookup-services/lookup.service";
import {catchError, of} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-legality-dropdown',
  standalone: false,

  templateUrl: './legality-dropdown.component.html',
  styleUrl: './legality-dropdown.component.css'
})
export class LegalityDropdownComponent implements AfterViewInit{
  @Input() legalityControl!: FormControl;
  legalities: {legalityId: number, legalityStatus: string}[] = [];

  constructor(private lookupService: LookupService) {}

  ngAfterViewInit() {
      this.fetchLegalities();
  }

  fetchLegalities() {
    this.lookupService.getLegalities()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown legality options:', err);
          return of([])
        })
      )
      .subscribe((data) => {this.legalities = data;
        if(!environment.production) {
          console.log('Legality dropdown options fetched:' + this.legalities);
        }});
  }
}
