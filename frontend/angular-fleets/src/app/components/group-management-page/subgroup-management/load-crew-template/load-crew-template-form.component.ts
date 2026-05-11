import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {
  CrewTemplateViewModel
} from "../../../../models/group-management-models/view-models/group-composition/crew-template-view-model";
import {
  AbstractStringDropdownComponent
} from "../../../dropdowns/abstract-string-string-map-dropdown/abstract-string-dropdown.component";
import {TemplateSelectDropdownComponent} from "./template-select-dropdown/template-select-dropdown.component";
import {BehaviorSubject, Subject, takeUntil} from "rxjs";
import {
  GroupCompositionApiService
} from "../../../../services/api-services/group-management/group-composition-api/group-composition-api.service";
import {AsyncPipe} from "@angular/common";

@Component({
  selector: 'app-load-crew-template-form',
  imports: [
    AbstractStringDropdownComponent,
    TemplateSelectDropdownComponent,
    AsyncPipe
  ],
  templateUrl: './load-crew-template-form.component.html',
  styleUrl: './load-crew-template-form.component.css'
})
export class LoadCrewTemplateFormComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>;

  @Output() cancelForm = new EventEmitter<void>();
  @Output() emitSelection = new EventEmitter<CrewTemplateViewModel>();

  private crewTemplates: CrewTemplateViewModel[] = [];
  public templatesForDisplay$ = new BehaviorSubject<CrewTemplateViewModel[]>([]);

  protected templateCategoryCtrl = new FormControl<string | null>(null);
  protected templateCategories = ['Capital', 'Large', 'Medium', 'Small', 'Custom'];
  public templateSelectCtrl = new FormControl<CrewTemplateViewModel | null>(null, Validators.required);

  constructor(private groupCompApi: GroupCompositionApiService){}

  ngOnInit() {
    this.groupCompApi.fetchCrewTemplateSummaries().pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (templates: CrewTemplateViewModel[]) => {
          this.crewTemplates = templates;
        }
      })

    this.templateCategoryCtrl.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        const filtered = this.crewTemplates.filter(
          template => template.templateCategory === value
        );
        this.templatesForDisplay$.next(filtered);
      })
  }

  onSubmit() {
    if(!this.templateSelectCtrl.value) {
      this.templateSelectCtrl.markAsDirty();
      this.templateSelectCtrl.markAsTouched();
      return;
    }

    this.emitSelection.emit(this.templateSelectCtrl.value);
    setTimeout(() => {
      this.templateCategoryCtrl.reset();
      this.templateSelectCtrl.reset();
    }, 500);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
