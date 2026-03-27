import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {Subject} from "rxjs";
import {NgSelectComponent} from "@ng-select/ng-select";
import {NgIf} from "@angular/common";
import {MatError} from "@angular/material/form-field";

@Component({
  selector: 'app-abstract-string-dropdown',
  standalone: true,
  templateUrl: './abstract-string-dropdown.component.html',
  imports: [
    NgSelectComponent,
    ReactiveFormsModule,
    MatError,
    NgIf
  ],
  styleUrl: './abstract-string-dropdown.component.css'
})
export class AbstractStringDropdownComponent implements OnInit, OnDestroy {
  private destroy$: Subject<void> = new Subject<void>();

  @Input() abstractControl!: FormControl;
  @Input() options: string[] = [];
  @Input() errorMessage?: string;
  @Input() placeholderLabel?: string;

  protected placeholder!: string;

  ngOnInit() {
    this.placeholder = 'Select ' + this.placeholderLabel + '...';
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
