import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatInput, MatSuffix} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-roster-text-field-filter',
  standalone: true,
  templateUrl: './roster-text-field-filter.component.html',
  imports: [
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatSuffix,
    NgIf,
    ReactiveFormsModule
  ],
  styleUrl: './roster-text-field-filter.component.css'
})
export class RosterTextFieldFilterComponent {

  @Input() textFieldFilterCtrl = new FormControl<string | null>(null);
  @Input() placeholder: string = 'Filter...';
  @Output() filterTermsEmitter = new EventEmitter<string | null>();

}
