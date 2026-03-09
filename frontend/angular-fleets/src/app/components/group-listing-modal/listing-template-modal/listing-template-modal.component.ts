import {AfterViewInit, Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {ListingTemplateViewModel} from "../../../models/listing-templates/listing-template-view-model";
import {
  TemplatesModalService
} from "../../../services/component-services/templates-modal-service/templates-modal.service";
import {DatePipe, NgClass, NgIf} from "@angular/common";
import {ConfirmGenericComponent} from "../../pop-ups/confirm-generic/confirm-generic.component";
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {
  ListingTemplatesApiService
} from "../../../services/api-services/listing-templates-api/listing-templates-api.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatIcon} from "@angular/material/icon";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-listing-template-modal',
  standalone: true,
  templateUrl: './listing-template-modal.component.html',
  imports: [
    NgIf,
    DatePipe,
    NgClass,
    MatMenu,
    MatMenuTrigger,
    MatIcon,
    MatMenuItem
  ],
  styleUrl: '../group-listing-modal.component.css'
})
export class ListingTemplateModalComponent implements AfterViewInit {
  @Input() templateModalIsVisible!: boolean;
  @Input() selectedTemplate: ListingTemplateViewModel | null = null;
  @Output() close = new EventEmitter();

  constructor(public templatesModal: TemplatesModalService,
              private templatesApi: ListingTemplatesApiService,
              private snackBar: MatSnackBar,
              private router: Router,
              private dialog: MatDialog) {
  }

  ngAfterViewInit() {
    if(!environment.production) {
      console.log("selectedTemplate: ", this.selectedTemplate);
    }
  }

  createFromTemplate() {
    this.templatesModal.closeModal(null);
    this.router.navigate(['/create-listing'], {
      state: { draft: this.selectedTemplate }
    });

  }

  openConfirmDelete(selected: ListingTemplateViewModel) {
    this.templatesModal.closeModal('delete');
    const message: string = "Please confirm deletion of:"
    const title: string | undefined = selected.listingTitle;

    const dialogRef = this.dialog.open(ConfirmGenericComponent, {
      data: {
        message: message,
        title: title
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if(result == true) {
        this.deleteTemplate(selected.templateId);
      }
    })

    this.selectedTemplate = null;
  }

  deleteTemplate(templateId: number) {
    this.templatesApi.deleteTemplate(templateId)
      .subscribe({
        next: (response: {message: string }) => {
          this.snackBar.open(`${response.message}`, 'OK', {
            duration: 4000,
            verticalPosition: 'top',
            horizontalPosition: 'center',
            panelClass: ['mobile-snackbar']
          });
          this.selectedTemplate = null;
          this.templatesModal.closeModal('delete');
        },
        error: err => {
          alert(`There was an error deleting the template: ${err.message}`);
        }
      })

  }
}
