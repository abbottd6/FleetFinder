import {DestroyRef, inject, Injectable} from '@angular/core';
import {
  GroupCompositionApiService
} from "../../api-services/group-management/group-composition-api/group-composition-api.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {
  CrewTemplateViewModel
} from "../../../models/group-management-models/view-models/group-composition/crew-template-view-model";
import {BehaviorSubject, Observable} from "rxjs";
import {
  GroupCompSubgroupViewModel
} from "../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";
import {
  GroupCompositionDto
} from "../../../models/group-management-models/view-models/group-composition/group-composition-dto";

@Injectable({
  providedIn: 'root'
})
export class SubgroupManagementInteractService {
  private destroyRef = inject(DestroyRef);

  protected subgroupTreesSubject = new BehaviorSubject<GroupCompSubgroupViewModel[]>([]);
  public subgroupTrees$ = this.subgroupTreesSubject.asObservable();

  constructor(private compositionApi: GroupCompositionApiService) {}

  getExistingSubgroupTrees(groupId: number) {
    this.compositionApi.getExistingGroupStructure(groupId).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (subgroupTrees: GroupCompositionDto) => {
          this.subgroupTreesSubject.next(subgroupTrees.subgroups);
        }
      })
  }

  createSubgroupFromTemplate(groupId: number, template: CrewTemplateViewModel) {
    this.compositionApi.createSubgroupFromTemplate(groupId, template).pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (responseDto: GroupCompositionDto) => {
          const newSubgroups = responseDto.subgroups;
          const currentList = this.subgroupTreesSubject.getValue();
          this.subgroupTreesSubject.next([
            ...currentList,
            ...newSubgroups
          ])
        }
      });
  }
}
