import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatTableDataSource, MatTableModule} from "@angular/material/table";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {SelectionModel} from "@angular/cdk/collections";
import {AsyncPipe, NgIf} from "@angular/common";
import {Router, RouterLink} from "@angular/router";
import {UserService} from "../../services/user-services/user.service";
import {map, Observable, shareReplay, Subject, takeUntil} from "rxjs";
import {BreakpointObserver} from "@angular/cdk/layout";
import {MatIcon} from "@angular/material/icon";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatDialog} from "@angular/material/dialog";
import {
  ListingViewInteractionsService
} from "../../services/facade-services/listing-view-interactions/listing-view-interactions.service";
import {
  DesktopTableViewComponent
} from "../listing-tables/desktop-table-view/desktop-table-view/desktop-table-view.component";
import {MobileFeedViewComponent} from "../listing-tables/mobile-feed-view/mobile-feed-view.component";
import {LayoutMode} from "../input-fields/search-bar/search-bar.component";
import {
  ListingOwnerActionsService
} from "../../services/facade-services/listing-view-interactions/listing-owner-actions.service";
import {ListingTemplatesApiService} from "../../services/api-services/listing-templates-api/listing-templates-api.service";
import {CreateTemplateRequest} from "../../models/listing-templates/create-template-request";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

@Component({
  selector: 'app-user-acct-listings-table',
  standalone: true,
  templateUrl: './user-acct-listings-table.component.html',
  styleUrl: './user-acct-listings-table.component.css',
  imports: [MatTableModule, MatCheckboxModule, RouterLink, AsyncPipe, NgIf, MatIcon, MatMenu, MatMenuTrigger, DesktopTableViewComponent, MobileFeedViewComponent, MatMenuItem],
})
export class UserAcctListingsTableComponent implements OnInit, OnChanges, OnDestroy {
  @Input() userListings: GroupListingViewModel[] = [];
  @Output() listingForModal = new EventEmitter<GroupListingViewModel>();

  private destroy$ = new Subject<void>();

  private breakpointObserver = inject(BreakpointObserver);
  readonly dialog = inject(MatDialog);

  fullColumns = [ 'select', 'title', 'status', 'category', 'pvp', 'system', 'roles', 'updated' ]
  mobileColumns = ['select', 'details']
  dataSource = new MatTableDataSource<GroupListingViewModel>();
  selection = new SelectionModel<GroupListingViewModel>(true, []);

  constructor(private listingOwnerSrv: ListingOwnerActionsService,
              private userService: UserService,
              protected listingInteract: ListingViewInteractionsService,
              private templatesApi: ListingTemplatesApiService,
              private snackBar: MatSnackBar) {}

  ngOnInit() {
    this.dataSource.data = this.userListings;
  }

  ngOnChanges(changes: SimpleChanges) {
    this.dataSource.data = this.userListings;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  emitChildClick(listing: GroupListingViewModel) {
    console.log("listing emitted: ", listing.listingTitle);
    this.listingForModal.emit(listing);
  }

  changeSelectedFromChild(selected: SelectionModel<GroupListingViewModel>) {
    this.selection = selected;
  }

  singleSelected() {
    return this.selection.selected.length < 2;
  }

  tableActionReset() {
    this.selection.clear()
    this.userService.refreshUser();
    this.dataSource.data = this.userListings
  }

  updateListing() {
    const selectedCount = this.selection.selected.length;
    if (selectedCount !== 1) {
      this.snackBar.open('Please select exactly one listing to update at a time.', 'OK',
        {duration: 4500, verticalPosition: 'top', horizontalPosition: 'center', panelClass: 'my-snackbar'})
        .onAction().pipe(takeUntil(this.destroy$)).subscribe(() => this.snackBar.dismiss());
      return;
    }
    const selected = this.selection.selected[0];
    this.listingOwnerSrv.userUpdateSelected(selected)
  }

  deleteListings() {
    this.listingOwnerSrv.openConfirmDelete(this.selection.selected);
    this.tableActionReset();
  }


  //TODO put a unique constraint on title/user in the db and check for templates with the same title
  createTemplateFromListing() {
    if(!this.singleSelected()) return;

    const selected = this.selection.selected[0];

    this.listingOwnerSrv.createTemplateFromListing(selected);
    this.tableActionReset();
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
