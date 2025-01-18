import {Component, Input, OnInit, ChangeDetectorRef} from '@angular/core';
import {LookupService} from "../../../services/api-lookup-services/lookup.service";
import {catchError, of} from "rxjs";
import {FormGroup} from "@angular/forms";

@Component({
    selector: 'app-server-dropdown',
    templateUrl: './server-dropdown.component.html',
    styleUrl: './server-dropdown.component.css',
    standalone: false
})
export class ServerDropdownComponent implements OnInit {
  @Input() parentForm!: FormGroup;
  servers: {serverId: number, name: string}[] = [];


  constructor(private lookupService: LookupService) {}

  ngOnInit(): void {
    this.fetchServerRegions();
  }

  fetchServerRegions(): void {
    this.lookupService.getServerRegions()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown server regions:', err);
          return of([]);
        })
      )
      .subscribe((data) => {
        this.servers = data;
        console.log('Server dropdown options fetched:', this.servers);
      })
  }
}
