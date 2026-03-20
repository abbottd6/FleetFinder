import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {DropdownModule} from "../../dropdowns/dropdown-module/dropdown.module";
import {
  CustomNoteFormService,
  CustomNotificationFormShape
} from "../../../services/custom-notification-form-service/custom-note-form.service";
import {BehaviorSubject, Subject} from "rxjs";
import { FormGroup } from '@angular/forms';
import {
  CustomNotificationViewModel
} from "../../../models/NotificationPrefAndCustomNotesModels/CustomNotificationViewModel";
import {environment} from "../../../../environments/environment";
import {
  AbstractStringDropdownComponent
} from "../../dropdowns/abstract-string-string-map-dropdown/abstract-string-dropdown.component";
import {LANGUAGE_OPTIONS} from "../../../models/language-options";
import {
  GenericSmallInputFieldComponent
} from "../../input-fields/generic-small-input-field/generic-small-input-field.component";
import {
  CreateOrUpdateCustomNoteRequest
} from "../../../models/NotificationPrefAndCustomNotesModels/CreateOrUpdateCustomNoteRequest";
import {NotificationApiService} from "../../../services/api-services/notification-api/notification-api.service";
import {AsyncPipe} from "@angular/common";

@Component({
  selector: 'app-custom-notification-form',
  standalone: true,
  templateUrl: './custom-notification-form.component.html',
  imports: [
    DropdownModule,
    AbstractStringDropdownComponent,
    GenericSmallInputFieldComponent,
    AsyncPipe
  ],
  styleUrl: './custom-notification-form.component.css'
})
export class CustomNotificationFormComponent implements OnInit, OnDestroy {
  @Output() closeForm = new EventEmitter<boolean>();

  private destroy$ = new Subject<void>();
  public customNoteFormSubmitted: boolean = false;

  private submitSubject = new BehaviorSubject<boolean>(false);
  public submitting$ = this.submitSubject.asObservable();

  customNoteForm!: FormGroup<CustomNotificationFormShape>;

  draft!: CustomNotificationViewModel | undefined;

  customNoteData?: CustomNotificationViewModel | undefined;

  constructor(protected noteFormService: CustomNoteFormService,
              private notesApi: NotificationApiService) {}

  ngOnInit() {
    this.customNoteForm = this.noteFormService.customNotificationForm;

    this.draft = history.state?.draft as CustomNotificationViewModel | undefined;
    this.customNoteData = this.draft as CustomNotificationViewModel;

    if(!environment.production) {
      console.log(this.draft);
    }

    if(this.draft){
      this.noteFormService.patchFromExisting(this.draft);
      this.draft = undefined;
    }
  }

  onSubmit() {
    this.submitSubject.next(true);
    setTimeout(() => this.submitSubject.next(false), 4000);
    if(this.customNoteForm.invalid) {
      this.customNoteForm.markAllAsTouched();
      this.customNoteFormSubmitted = true;
      return;
    }

    const newCustomNoteData = new CreateOrUpdateCustomNoteRequest(this.customNoteForm.controls);

    if(!environment.production) {
      console.log(newCustomNoteData);
    }

    this.notesApi.createCustomNotification(newCustomNoteData).subscribe({
      next: response => {
        if(!environment.production) {
          console.log(response.tagLabel);
        }
        this.closeForm.emit(true);
        this.customNoteData = undefined;
        this.customNoteForm.reset();
      },
      error: err => {
        alert(err.status)
      }
    })
  }

  cancel() {
    this.closeForm.emit(true);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly LANGUAGE_OPTIONS = LANGUAGE_OPTIONS;
}
