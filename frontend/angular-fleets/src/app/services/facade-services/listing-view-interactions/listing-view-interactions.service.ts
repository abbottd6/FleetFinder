import {DestroyRef, inject, Injectable} from '@angular/core';
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {combineLatest, map, Observable, of, Subject, take} from "rxjs";
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

  selectedListing: GroupListingViewModel | null = null;
  isModalVisible: boolean = false;
  refreshSubject = new Subject<'hide' | 'bookmark' | 'unbookmark' | 'report' | null>();
  readonly refresh$ = this.refreshSubject.asObservable();

  bookmarkedIds$!: Observable<Set<number>>;
  selectedIsBookmarked$!: Observable<boolean>;

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
  }

  //on-row-click instructions for groupListing modal popup
  onRowClick(tempListing: GroupListingViewModel) {
    this.selectedListing = tempListing;
    this.selectedIsBookmarked$ = combineLatest([
      this.bookmarkedIds$,
      of(this.selectedListing.groupId),
    ]).pipe(
      map(([ids, selectedId]) => !!selectedId && ids.has(selectedId))
    );
    this.isModalVisible = true;
  }

  isBookmarked(id: number, bookmarkIds: Set<number> | null): boolean {
    if(bookmarkIds == undefined) {
      return false;
    }
    return !!bookmarkIds && bookmarkIds.has(id);
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
    this.selectedListing = null;

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
