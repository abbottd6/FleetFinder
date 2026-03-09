import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {LookupService} from "../../../services/api-services/reference-data-api/lookup.service";
import {catchError, of} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";
import {environment} from "../../../../environments/environment";
import {GameExperience} from "../../../models/reference-data/reference-data.models";

@Component({
  selector: 'app-experience-dropdown',
  standalone: false,

  templateUrl: './experience-dropdown.component.html',
  styleUrl: './experience-dropdown.component.css'
})
export class ExperienceDropdownComponent implements AfterViewInit{
  @Input() experienceControl!: FormControl;
  experiences: GameExperience[] = [];

  constructor(private lookupService: LookupService) {}

  ngAfterViewInit() {
    this.fetchGameExperiences();
  }

  fetchGameExperiences(): void {
    this.lookupService.getGameExperiences()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown game experiences', err);
          return of([]);
        })
      )
      .subscribe((data) => {this.experiences = data;
        if(!environment.production) {
          console.log('Experiences dropdown options fetched:', this.experiences);
        }
      });
  }
}
