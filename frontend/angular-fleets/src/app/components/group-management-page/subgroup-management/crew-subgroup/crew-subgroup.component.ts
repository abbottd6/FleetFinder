import {Component, Input, OnInit} from '@angular/core';
import {
  GroupCompSubgroupViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";
import {CrewPositionChipComponent} from "../crew-position-chip/crew-position-chip.component";
import {NgForOf, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {BehaviorSubject} from "rxjs";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";

@Component({
  selector: 'app-crew-subgroup',
  imports: [
    CrewPositionChipComponent,
    NgForOf,
    NgIf,
    MatIcon,
    MatTooltip,
    MatMenuTrigger,
    MatMenu,
    MatMenuItem,
  ],
  templateUrl: './crew-subgroup.component.html',
  styleUrl: './crew-subgroup.component.css'
})
export class CrewSubgroupComponent implements OnInit {
  @Input() subgroup!: GroupCompSubgroupViewModel;
  @Input() collapseFromParent$!: BehaviorSubject<boolean>;

  protected collapseFromSelf$ = new BehaviorSubject<boolean>(true);

  protected selfExpanded: boolean = true;
  protected childrenExpanded: boolean = true;

  protected get assignedPositionsCount(): number {
    return this.subgroup.crewPositions.filter(p => p.assignedMember != null).length;
  }

  protected get totalPositionsCount(): number {
    return this.subgroup.crewPositions.length + this.subgroup.subgroups.reduce((sum, subgroup) =>
      sum + this.recursivelyCountPositionsInTree(subgroup), 0);
  }

  protected get totalSubgroupsCount(): number {
    return this.subgroup.subgroups.length + this.subgroup.subgroups.reduce((sum, subgroup) =>
      sum + this.recursivelyCountSubgroupChildren(subgroup), 0);
  }

  private recursivelyCountPositionsInTree(subgroup: GroupCompSubgroupViewModel): number {
    return subgroup.crewPositions.length +
      subgroup.subgroups.reduce((sum, child) =>
        sum + this.recursivelyCountPositionsInTree(child), 0);
  }

  private recursivelyCountSubgroupChildren(subgroup: GroupCompSubgroupViewModel): number {
    return subgroup.subgroups.length +
      subgroup.subgroups.reduce((sum, child) =>
        sum + this.recursivelyCountSubgroupChildren(child), 0);
  }

  protected get hasChildSubgroups(): boolean {
    return this.subgroup.subgroups.length > 0;
  }

  ngOnInit() {
    if(this.collapseFromParent$ != null) {
      this.collapseFromParent$.subscribe(collapse => {
        this.selfExpanded = collapse;
        this.childrenExpanded = collapse;
        this.collapseFromSelf$.next(collapse);
      })
    }
  }

  toggleCollapseSelf() {
    this.selfExpanded = !this.selfExpanded;

    this.childrenExpanded = this.selfExpanded;

    this.collapseFromSelf$.next(this.selfExpanded);
  }

  collapseChildren() {
    if(!this.selfExpanded) {
      this.selfExpanded = true;
    }

    this.childrenExpanded = !this.childrenExpanded;
    this.collapseFromSelf$.next(this.childrenExpanded);
  }
}
