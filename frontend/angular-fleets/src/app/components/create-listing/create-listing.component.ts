import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {LookupService} from "../../services/lookup.service";

@Component({
  selector: 'app-create-listing',
  templateUrl: './create-listing.component.html',
  styleUrl: './create-listing.component.css',
})
export class CreateListingComponent implements OnInit{

  //this was throwing an error, need to check if this should be undefined (suggested)
  //or if there is a different problem.
  createListingFormGroup!: FormGroup;

  public environments: any[] = [];
  public experiences: any[] = [];
  public playStyles: any[] = [];
  public legalities: any[] = [];
  public groupStatuses: any[] = [];
  public categories: any[] = [];
  public subcategories: any[] = [];
  public pvpStatuses: any[] = [];
  public planetarySystems: any[] = [];
  public planetMoonSystems:  any[] = [];

  constructor(private formBuilder: FormBuilder, private lookupService: LookupService) {

  }
  ngOnInit(): void {



    this.lookupService.getGameEnvironments().subscribe(data => {
      this.environments = data;
    })

    this.lookupService.getGameExperiences().subscribe(data => {
      this.experiences = data;
    })

    this.lookupService.getPlayStyles().subscribe(data => {
      this.playStyles = data;
    })

    this.lookupService.getLegalities().subscribe(data => {
      this.legalities = data;
    })

    this.lookupService.getGroupStatuses().subscribe(data => {
      this.groupStatuses = data;
    })

    this.lookupService.getGameplayCategories().subscribe(data => {
      this.categories = data;
    })

    this.lookupService.getGameplaySubcategories().subscribe(data => {
      this.subcategories = data;
    })

    this.lookupService.getPvpStatuses().subscribe(data => {
      this.pvpStatuses = data;
    })

    this.lookupService.getPlanetarySystems().subscribe(data => {
      this.planetarySystems = data;
    })

    this.lookupService.getPlanetMoonSystems().subscribe(data => {
      this.planetMoonSystems = data;
    })


    //Form builder
    this.createListingFormGroup = this.formBuilder.group({
      groupListing: this.formBuilder.group({
        serverId: [null, Validators.required],
        environmentId: [null, Validators.required],
        experienceId: [null, Validators.required],
        listingTitle: ['', Validators.required, Validators.maxLength(65)],
        styleId: [null, Validators.required],
        legalityId: [null, Validators.required],
        groupStatusId: [null, Validators.required],
        eventSchedule: [{value: null, disabled: true}],
        categoryId: [null, Validators.required],
        subcategoryId: [null],
        pvpStatusId: [null, Validators.required],
        systemId: [null, Validators.required],
        planetId: [null],
        listingDescription: ['', Validators.required, Validators.maxLength(500)],
        desiredPartySize: ['', Validators.required, Validators.min(2), Validators.max(1000)],
        currentPartySize: ['', Validators.required, Validators.min(1), Validators.max(1000)],
        availableRoles: ['', Validators.required, Validators.maxLength(100)],
        commsOption: [null, Validators.required],
        commsService: ['', Validators.maxLength(25)],
      })
    });
  }

}
