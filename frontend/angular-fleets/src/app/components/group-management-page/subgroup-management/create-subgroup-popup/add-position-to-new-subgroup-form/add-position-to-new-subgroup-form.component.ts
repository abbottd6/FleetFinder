import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {Subject, takeUntil} from "rxjs";
import {
  GroupCompositionApiService
} from "../../../../../services/api-services/group-management/group-composition-api/group-composition-api.service";
import {
  RoleClassSummaryViewModel
} from "../../../../../models/group-management-models/nested-models/role-class-summary-view-model";
import {GROUP_COMP_ROLE_CATEGORIES} from "../../create-or-edit-position-popup/create-or-edit-position-popup.component";
import {
  GroupManagementInteractService
} from "../../../../../services/facade-services/group-management/group-management-interact.service";
import {
  AbstractStringDropdownComponent
} from "../../../../dropdowns/abstract-string-string-map-dropdown/abstract-string-dropdown.component";
import {MatIcon} from "@angular/material/icon";
import {NgSelectComponent} from "@ng-select/ng-select";
import {FormControl, ReactiveFormsModule} from "@angular/forms";

@Component({
  selector: 'app-add-position-to-new-subgroup-form',
  imports: [
    AbstractStringDropdownComponent,
    MatIcon,
    NgSelectComponent,
    ReactiveFormsModule
  ],
  templateUrl: './add-position-to-new-subgroup-form.component.html',
  styleUrl: './add-position-to-new-subgroup-form.component.css'
})
export class AddPositionToNewSubgroupFormComponent implements OnInit, OnDestroy{
  private destroy$ = new Subject<void>();

  @Output() addPosition = new EventEmitter<RoleClassSummaryViewModel>();

  protected roleCategories = GROUP_COMP_ROLE_CATEGORIES;
  protected availableClasses: RoleClassSummaryViewModel[] = [];
  protected filteredClasses: RoleClassSummaryViewModel[] = [];

  protected roleCategoryCtrl: FormControl<string | null> = new FormControl<string | null>(null, {
    nonNullable: true
  })
  protected selectedRoleCtrl: FormControl<RoleClassSummaryViewModel | null> = new FormControl<RoleClassSummaryViewModel | null>({
    value: null, disabled: true}, {
    nonNullable: true
  })

  constructor(private compositionApi: GroupCompositionApiService,
              private managementInteract: GroupManagementInteractService) {}

  ngOnInit() {

    this.compositionApi.getAvailableRoleClassifications(this.managementInteract.groupId).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (classifications: RoleClassSummaryViewModel[]) => {
          this.availableClasses = classifications;
        },
        error: () => {
          this.managementInteract.showSnackBarMessage('Failed to fetch crew position reference data.')
        }
      })

    this.roleCategoryCtrl.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(category => {
        this.filteredClasses = this.availableClasses.filter(c => c.roleCategory == category);

        if(category == null) {
          this.selectedRoleCtrl.reset();
          this.selectedRoleCtrl.disable();
        } else {
          this.selectedRoleCtrl.enable();
        }
      })

  }

  emitAddPosition() {
    if(!this.selectedRoleCtrl.value) {
      this.managementInteract.showSnackBarMessage('Error: select a role before adding.');
      this.selectedRoleCtrl.markAsDirty();
      this.selectedRoleCtrl.markAsTouched();
      return;
    } else {
      this.addPosition.emit(this.selectedRoleCtrl.value);
      this.selectedRoleCtrl.reset();
      this.roleCategoryCtrl.reset();
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
