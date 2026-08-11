import {AfterViewInit, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {
  GroupCompSubgroupViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatError, MatFormField, MatInput} from "@angular/material/input";
import {NgIf} from "@angular/common";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {
  GroupCompositionInteractService
} from "../../../../services/facade-services/group-management/group-composition-interact.service";

@Component({
  selector: 'app-edit-subgroup-label-input',
  imports: [
    FormsModule,
    MatError,
    MatFormField,
    NgIf,
    ReactiveFormsModule,
    MatInput,
    MatFormFieldModule,
    MatIcon
  ],
  templateUrl: './edit-subgroup-label-input.component.html',
  styleUrl: './edit-subgroup-label-input.component.css'
})
export class EditSubgroupLabelInputComponent implements OnInit, AfterViewInit {
  @Input() subgroup!: GroupCompSubgroupViewModel;
  @ViewChild('inputField') inputFieldRef!: ElementRef<HTMLInputElement>
  @Output() cancelTitleEdit = new EventEmitter<boolean>;
  @Output() emitEditingTitle = new EventEmitter<string>;
  characterCount: number = 0;

  protected subgroupLabelCtrl = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required,
                 Validators.minLength(1),
                 Validators.maxLength(32)]
  });

  constructor(private subgroupInteract: GroupCompositionInteractService){};

  ngOnInit(){
    this.subgroupLabelCtrl.setValue(this.subgroup.subgroupLabel);

    this.characterCount = this.subgroupLabelCtrl.value.length;
  }

  ngAfterViewInit() {
    this.inputFieldRef.nativeElement.focus();
  }

  updateCharacterCount() {
    const value = this.subgroupLabelCtrl.value || '';
    this.characterCount = value.length;
  }

  updateLabel() {
    this.emitEditingTitle.emit(this.subgroupLabelCtrl.value);
  }

  cancel() {
    this.cancelTitleEdit.emit(true);
  }
}
