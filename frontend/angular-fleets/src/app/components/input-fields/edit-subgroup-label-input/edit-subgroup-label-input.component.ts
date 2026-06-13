import {Component, Input, OnInit} from '@angular/core';
import {
  GroupCompSubgroupViewModel
} from "../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatError, MatFormField, MatHint, MatInput, MatLabel} from "@angular/material/input";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-edit-subgroup-label-input',
  imports: [
    FormsModule,
    MatError,
    MatFormField,
    MatHint,
    MatLabel,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './edit-subgroup-label-input.component.html',
  styleUrl: './edit-subgroup-label-input.component.css'
})
export class EditSubgroupLabelInputComponent implements OnInit {
  @Input() subgroup!: GroupCompSubgroupViewModel;
  characterCount: number = 0;

  protected subgroupLabelCtrl = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required,
                 Validators.minLength(3),
                 Validators.maxLength(32)]
  });

  ngOnInit(){
    this.subgroupLabelCtrl.setValue(this.subgroup.subgroupLabel);
  }

  updateCharacterCount() {
    const value = this.subgroupLabelCtrl.value || '';
    this.characterCount = value.length;
  }
}
