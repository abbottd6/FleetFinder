import {Component, Input} from '@angular/core';
import {MatError} from "@angular/material/input";
import {NgIf} from "@angular/common";
import {NgSelectComponent} from "@ng-select/ng-select";
import {FormControl, ReactiveFormsModule, Validators} from "@angular/forms";
import {
  CrewTemplateViewModel
} from "../../../../../models/group-management-models/view-models/group-composition/crew-template-view-model";

@Component({
  selector: 'app-template-select-dropdown',
  imports: [
    NgSelectComponent,
    ReactiveFormsModule
  ],
  templateUrl: './template-select-dropdown.component.html',
  styleUrl: './template-select-dropdown.component.css'
})
export class TemplateSelectDropdownComponent {

  @Input() filteredOptions!: CrewTemplateViewModel[] | null;
  @Input() templateSelectCtrl!: FormControl<CrewTemplateViewModel | null>;

}
