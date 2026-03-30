import {Component, ElementRef, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {DropdownModule} from "../../dropdowns/dropdown-module/dropdown.module";
import {
  CustomNoteFormService,
  CustomNotificationFormShape
} from "../../../services/custom-notification-form-service/custom-note-form.service";
import {BehaviorSubject, distinctUntilChanged, filter, Subject, takeUntil} from "rxjs";
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
import {AsyncPipe} from "@angular/common";
import {
  NotificationSettingsApiService
} from "../../../services/api-services/notification-api/notification-settings-api.service";
import {HttpErrorResponse} from "@angular/common/http";
import {MatSnackBar} from "@angular/material/snack-bar";
import {
  CustomNotificationService
} from "../../../services/facade-services/custom-notification-service/custom-notification.service";

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
  @Input() noteForEdit?: CustomNotificationViewModel;
  @ViewChild('noteForm') noteForm!: ElementRef;

  private destroy$ = new Subject<void>();
  public customNoteFormSubmitted: boolean = false;

  private submitSubject = new BehaviorSubject<boolean>(false);
  public submitting$ = this.submitSubject.asObservable();

  customNoteForm!: FormGroup<CustomNotificationFormShape>;

  draft!: CustomNotificationViewModel | undefined;
  editingId: number | null = null;

  customNoteData?: CustomNotificationViewModel | undefined;

  constructor(protected noteFormService: CustomNoteFormService,
              private noteSettingsApi: NotificationSettingsApiService,
              private customNoteService: CustomNotificationService,
              private snackBar: MatSnackBar) {

    this.customNoteService.customNoteForEdit$.pipe(
      takeUntil(this.destroy$),
      distinctUntilChanged(),
      filter(editNote => editNote !== null),
    ).subscribe(editNote => {
        if(editNote) {
          this.closeForm.emit(false);
          this.noteForm.nativeElement.scrollIntoView({ behavior: "smooth", block: 'start' });
          this.noteFormService.patchFromExisting(editNote);
          this.editingId = editNote.customNoteId;
        }
      })
  }

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

    if(!this.editingId) {
      this.noteSettingsApi.createCustomNotification(newCustomNoteData).subscribe({
        next: response => {
          if (!environment.production) {
            console.log(response.tagLabel);
          }
          this.closeForm.emit(true);
          this.customNoteData = undefined;
          this.customNoteService.resetNoteForEdit();
          this.customNoteForm.reset();
        },
        error: err => {
          if (err instanceof HttpErrorResponse && err.status === 403) {
            this.snackBar.open('Error: There is a limit of 10 custom notifications per user.', 'OK', {
              duration: 4000,
              verticalPosition: 'top',
              horizontalPosition: 'center',
              panelClass: ['mobile-snackbar']
            })
          } else {
            this.snackBar.open('There was an error creating this custom notification.', 'OK', {
              duration: 4000,
              verticalPosition: 'top',
              horizontalPosition: 'center',
              panelClass: ['mobile-snackbar']
            })
          }
        }
      })
    } else {
      this.noteSettingsApi.editCustomNotificationData(this.editingId,newCustomNoteData).subscribe({
        next: response => {
          if (!environment.production) {
            console.log(response.tagLabel);
          }
          this.closeForm.emit(true);
          this.customNoteData = undefined;
          this.customNoteService.resetNoteForEdit();
          this.customNoteForm.reset();
          this.editingId = null;
        },
        error: err => {
          if (err instanceof HttpErrorResponse && err.status === 403) {
            this.snackBar.open('Error: There is a limit of 10 custom notifications per user.', 'OK', {
              duration: 4000,
              verticalPosition: 'top',
              horizontalPosition: 'center',
              panelClass: ['mobile-snackbar']
            })
          } else {
            this.snackBar.open('There was an error creating this custom notification.', 'OK', {
              duration: 4000,
              verticalPosition: 'top',
              horizontalPosition: 'center',
              panelClass: ['mobile-snackbar']
            })
          }
        }
      })
    }
  }

  cancel() {
    this.customNoteForm.reset();
    this.customNoteService.resetNoteForEdit();
    this.editingId = null;
    this.closeForm.emit(true);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly LANGUAGE_OPTIONS = LANGUAGE_OPTIONS;
}
