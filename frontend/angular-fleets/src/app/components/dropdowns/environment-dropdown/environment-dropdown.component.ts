import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-lookup-services/lookup.service";
import {catchError, of} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-environment-dropdown',
  standalone: false,

  templateUrl: './environment-dropdown.component.html',
  styleUrl: './environment-dropdown.component.css'
})
export class EnvironmentDropdownComponent implements AfterViewInit{
  @Input() environmentControl!: FormControl;
  environments: {environmentId: number, environmentType: string}[] = [];


  constructor(private lookupService: LookupService) {}

  ngAfterViewInit() {
    this.fetchGameEnvironments();
    console.log('Environment dropdown options fetched: ' + this.environments);
  }

  fetchGameEnvironments(): void {
    this.lookupService.getGameEnvironments()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown game environments:', err);
          return of([]);
        })
      )
      .subscribe((data) => {this.environments = data;});
  }
}
