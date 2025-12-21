import {DestroyRef, inject, Injectable} from '@angular/core';
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {BehaviorSubject, combineLatest, distinctUntilChanged, map, Observable, of, Subject, take} from "rxjs";
import {BookmarkApiService} from "../../api-services/bookmarks-api/bookmark-api.service";
import {environment} from "../../../../environments/environment";
import {ConfirmReportComponent} from "../../../components/pop-ups/confirm-report/confirm-report.component";
import {AddBookmarkRequest} from "../../../models/bookmark-requests/add-bookmark-request";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatDialog} from "@angular/material/dialog";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {ListingReportApiService} from "../../api-services/listing-reports-api/listing-report-api.service";
import {HideListingRequest} from "../../../models/listing-filter/hide-listing-request.model";
import {HiddenListingsApiService} from "../../api-services/hidden-listings-api/hidden-listings-api.service";
import {SubmitListingReport} from "../../../models/report-requests/submit-listing-report";
import {UiPrefsService} from "../ui-prefs/ui-prefs.service";
import {CloseValue} from "../../../components/group-listing-modal/group-listing-modal.component";

@Injectable({
  providedIn: 'root'
})
export class ListingViewInteractionsService {

  private destroyRef = inject(DestroyRef)
  isLoggedIn!: boolean;

  selectedListingSubject = new BehaviorSubject<GroupListingViewModel | null>(null);
  readonly selectedListing$ = this.selectedListingSubject.asObservable();

  bookmarkedIds$!: Observable<Set<number>>;
  readonly selectedIsBookmarked$!: Observable<boolean>;
  public longPressTriggered = false;

  isModalVisible: boolean = false;
  refreshSubject = new Subject<'hide' | 'bookmark' | 'unbookmark' | 'report' | null>();
  readonly refresh$ = this.refreshSubject.asObservable();

  constructor(private bmService: BookmarkApiService,
              private reportService: ListingReportApiService,
              private hideService: HiddenListingsApiService,
              protected uiPrefService: UiPrefsService,
              private snackBar: MatSnackBar,
              private dialog: MatDialog) {

    this.bmService.getBookmarksBrief();

    this.bookmarkedIds$ = this.bmService.bookmarksBrief$.pipe(
      map((gIds: number[]) => new Set<number>(gIds))
    );

    this.selectedIsBookmarked$ = combineLatest([
      this.bookmarkedIds$,
      this.selectedListing$,
    ]).pipe(
      map(([ids, listing]) => !!listing && ids.has(listing.groupId)),
      distinctUntilChanged()
    );
  }

  setSelectedListing(row: GroupListingViewModel | null) {
    this.selectedListingSubject.next(row);
  }

  get selectedListing(): GroupListingViewModel | null {
    return this.selectedListingSubject.value;
  }

  //on-row-click instructions for groupListing modal popup
  onRowClick(tempListing: GroupListingViewModel) {
    if(this.longPressTriggered) return;
    this.setSelectedListing(tempListing);
    this.uiPrefService.saveRowClick(tempListing.groupId);

    this.isModalVisible = true;
  }

  //modal popup cancel click for interactable cell
  bookmarkWrapper(event: MouseEvent, listingId: number) {
    event.stopPropagation();

    this.addBookmark(listingId)
  }

  unmarkWrapper(event: MouseEvent, listingId: number) {
    event.stopPropagation();

    this.deleteBookmark(listingId);
  }

  isBookmarked(id: number, bookmarkIds: Set<number> | null): boolean {
    if(bookmarkIds == undefined) {
      return false;
    }
    return !!bookmarkIds && bookmarkIds.has(id);
  }

  bookmarkSelected(): void {
    const listingId = this.selectedListing?.groupId;

    if(listingId == null) return;

    this.addBookmark(listingId)
  }

  addBookmark(listingId: number) {
    if(!this.isLoggedIn) {
      this.snackBar.open("You must log in to access bookmarks.", 'OK', {
        duration: 5000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['mobile-snackbar']})
      return;
    }
    const request = new AddBookmarkRequest(listingId);
    this.bmService.addBookmark(request).pipe(takeUntilDestroyed(this.destroyRef)).subscribe( {
      next: (response: { listingTitle: string; }) => {
        this.snackBar.open(`"${response.listingTitle}" added to bookmarks.`, 'OK', {
          duration: 4000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['mobile-snackbar']
        });

        this.uiPrefService.displayBookmarkListingHint();
        this.emitRefresh('bookmark');
      },
      error: (err) => {
        console.error(err);
      }
    })
  }

  deleteSelectedBookmark(): void {
    const listingId = this.selectedListing?.groupId;
    if(listingId == null) return;
    this.deleteBookmark(listingId);
  }

  deleteBookmark(listingId: number) {
    const request = listingId;
    if (!environment.production) {
      console.log(request);
    }
    this.bmService.deleteBookmark(request).pipe(takeUntilDestroyed(this.destroyRef)).subscribe( {
        next: (response: { message: string; }) => {
          this.snackBar.open(`${response.message}`, 'OK', {
            duration: 3000,
            verticalPosition: 'top',
            horizontalPosition: 'center',
            panelClass: ['mobile-snackbar']
          });
          this.emitRefresh('unbookmark');
        }
      }
    )
  }

  deleteMultipleBms(selected: GroupListingViewModel[]) {
    const bmGroupIds = selected.map(bm => bm.groupId);

    this.bmService.deleteMultipleBookmarks(bmGroupIds).pipe(takeUntilDestroyed(this.destroyRef)).subscribe( {
      next: (response: { message: string; }) => {
        this.snackBar.open(`${response.message}`, 'OK', {
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['mobile-snackbar']
        });
        this.emitRefresh('unbookmark')
      }
    })
  }

  hideSelected(): void {
    const listingId = this.selectedListing?.groupId;
    if(listingId == null) return;
    this.userHideListing(listingId);
  }

  userHideListing(listingId: number) {
    if(!this.isLoggedIn) {
      this.snackBar.open("You must log in to hide listings.", 'OK', {
        duration: 5000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['mobile-snackbar']})
      return;
    }
    const request = new HideListingRequest(listingId);
    this.hideService.addHidden(request).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (response: { Response: string; }) => {
        this.snackBar.open(`${response.Response}`, 'OK', {
          duration: 4000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['mobile-snackbar']
        });
        this.uiPrefService.displayHideListingHint();
        this.emitRefresh('hide');
      },

      error: (err) => {
        console.error(err);
      }
    })
  }

  reportSelected(): void {
    const listing = this.selectedListing;
    if(listing == null) return;
    this.openConfirmReport(listing)
  }

  openConfirmReport(listing: GroupListingViewModel): void {
    if(!this.isLoggedIn) {
      this.snackBar.open("You must log in to submit reports.", 'OK', {
        duration: 5000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
        panelClass: ['mobile-snackbar']})
      return;
    }

    this.reportService.reportOptions$
      .pipe(take(1))
      .subscribe(options => {
        const dialogRef = this.dialog.open(ConfirmReportComponent, {
          data: {
            listing,
            options
          }
        });

        dialogRef.afterClosed().pipe(takeUntilDestroyed(this.destroyRef)).subscribe(selected => {
          if (selected) {
            this.submitReport(listing.groupId, selected);
          }
        });
      });
  }

  submitReport(listingId: number, basisId: number) {
    const lr = new SubmitListingReport(listingId, basisId)

    this.reportService.submitReport(lr).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (response: { reportId: string; }) => {
        this.snackBar.open(`Report submitted. Thank you.`, 'OK', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          panelClass: ['mobile-snackbar']
        });

        this.uiPrefService.displayReportedListingHint();
        this.emitRefresh('report');
      },

      error: (err) => {
        console.error(err);
      }
    });
  }

  private emitRefresh(reason: 'hide' | 'bookmark' | 'unbookmark' | 'report') {
    this.refreshSubject.next(reason);
  }

  //on close instructions for groupListing modal popup
  onModalClose(action: CloseValue) {
    if(!environment.production) {
      console.log("Modal closed");
    }
    this.isModalVisible = false;
    this.setSelectedListing(null);

    if(!action.value) return;

    if(action.value && action.group) {
      switch (action.value) {
        case 'hide':
          return this.userHideListing(action.group.groupId);
        case 'bookmark':
          return this.addBookmark(action.group.groupId);
        case 'unbookmark':
          return this.deleteBookmark(action.group.groupId);
        case 'report':
          return this.openConfirmReport(action.group)
      }
    }
  }
}
