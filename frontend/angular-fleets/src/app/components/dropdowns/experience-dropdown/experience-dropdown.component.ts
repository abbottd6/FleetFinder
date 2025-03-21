import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-lookup-services/lookup.service";
import {catchError, of} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-experience-dropdown',
  standalone: false,

  templateUrl: './experience-dropdown.component.html',
  styleUrl: './experience-dropdown.component.css'
})
export class ExperienceDropdownComponent implements AfterViewInit{
  @Input() experienceControl!: FormControl;
  experiences: {experienceId: number, experienceType: string}[] = [];

  constructor(private lookupService: LookupService) {}

  ngAfterViewInit() {
    this.fetchGameExperiences();
    console.log('Experience dropdown options fetched: ' + this.experiences);
  }

  fetchGameExperiences(): void {
    this.lookupService.getGameExperiences()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown game experiences', err);
          return of([]);
        })
      )
      .subscribe((data) => {this.experiences = data;});
  }
}
