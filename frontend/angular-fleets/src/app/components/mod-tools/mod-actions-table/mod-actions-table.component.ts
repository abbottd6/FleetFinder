import {AfterViewInit, Component, OnDestroy, ViewChild} from '@angular/core';
import {AsyncPipe, DatePipe, NgIf} from "@angular/common";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable, MatTableDataSource
} from "@angular/material/table";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {MatSort, MatSortHeader} from "@angular/material/sort";
import {map, Observable, shareReplay, Subject, takeUntil} from 'rxjs';
import {BreakpointObserver} from "@angular/cdk/layout";
import {ModListingActionViewModel} from "../../../models/moderation/ModListingActionViewModel";
import {ModApiService} from "../../../services/api-services/mod-api/mod-api.service";
import {UiPrefsService} from "../../../services/facade-services/ui-prefs/ui-prefs.service";
import {LayoutMode} from "../../input-fields/search-bar/search-bar.component";

import {Page} from "../../../models/page-interface";

@Component({
  selector: 'app-mod-actions-table',
  standalone: true,
  templateUrl: './mod-actions-table.component.html',
  imports: [
    AsyncPipe,
    DatePipe,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatPaginator,
    MatRow,
    MatRowDef,
    MatTable,
    NgIf,
    MatHeaderCellDef
  ],
  styleUrl: './mod-actions-table.component.css'
})
export class ModActionsTableComponent implements OnDestroy, AfterViewInit{
  private actionsPanelDestroy$ = new Subject<void>();
  private breakpointObserver = new BreakpointObserver();

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  fullColumns: string[] = ['actionId', 'recordId', 'userId', 'username', 'modId', 'modName',
    'actionType', 'actionNote', 'actionTs']

  mobileColumns: string[] = ['actionId', 'modName', 'actionType', 'actionTs']
  readonly displayedColumns$!: Observable<string[]>;

  dataSource = new MatTableDataSource<ModListingActionViewModel>();
  noResults: boolean = true;

  pageIdx: number = 0;
  pageSz: number = 10;
  totalElements: number = 0;

  constructor(private modApi: ModApiService,
              private uiPrefService: UiPrefsService){

    this.loadWeeksActions(this.pageIdx, this.pageSz);

    this.uiPrefService.uiPrefs = this.uiPrefService.loadUiPrefs();

    this.displayedColumns$ = this.layoutMode$.pipe(
      map(mode => mode === 'handheld' ? this.mobileColumns : this.fullColumns)
    );
  }

  ngAfterViewInit() {
    this.paginator.page.pipe(takeUntil(this.actionsPanelDestroy$))
      .subscribe((event: PageEvent) => {
      this.pageIdx = event.pageIndex;
      this.pageSz = event.pageSize;
      this.loadWeeksActions(this.pageIdx, this.pageSz);
    })
  }

  ngOnDestroy() {
    this.actionsPanelDestroy$.next();
    this.actionsPanelDestroy$.complete();
  }

  loadWeeksActions(idx: number, sz: number) {
    this.modApi.modGetWeeksActions(idx, sz).pipe(takeUntil(this.actionsPanelDestroy$))
      .subscribe({
        next: (page: Page<ModListingActionViewModel>) => {
          this.dataSource.data = page.content;
          this.totalElements = page.page.totalElements;
          this.pageSz = page.page.size;
          this.pageIdx = page.page.number;
          this.noResults = (this.dataSource.data.length === 0);
        }
      })
  }

  layoutMode$: Observable<LayoutMode> = this.breakpointObserver
    .observe([
      '(max-width: 900px)',
      '(min-width: 901px) and (max-width: 1650px)',
      '(min-width: 1051px)'
    ])
    .pipe(
      map(state => {
        if (state.breakpoints['(max-width: 900px)']) {
          return 'handheld';
        }
        if (state.breakpoints['(min-width: 901px) and (max-width: 1650px)']) {
          return 'mobile';
        }

        return 'full';
      }),
      shareReplay(1)
    );
}
