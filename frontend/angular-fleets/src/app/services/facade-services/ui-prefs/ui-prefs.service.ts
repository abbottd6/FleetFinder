import {DestroyRef, inject, Injectable} from '@angular/core';
import {FilterService, PersistedFilterState} from "../../api-services/filter-api/filter.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {UiCleanupService} from "../../cleanup-services/ui-cleanup.service";
import {DontShowMeAgainPopup} from "../../../components/pop-ups/dont-show-me-again-popup/dont-show-me-again-popup";
import {MatDialog} from "@angular/material/dialog";

export const UI_PREFS_KEY = 'ff_ui_prefs';
export const MAX_CLICKED = 300;
export const CLICKED_EVICT_COUNT = 1;
export const ONE_DAY_MS = 24 * 60 * 60 * 1000;
export const SOFT_MAX_CLICKED = 3;

export interface UiPrefs {
  clickedRowIds: Set<number>;
  lastClickedClean: number;
  hideHiddenListingHint: boolean;
  hideReportedListingHint: boolean;
  hideBookmarkedListingHint: boolean;
  hideQuickAccessMenuHint: boolean;
  storedFilters: PersistedFilterState;
}

@Injectable({
  providedIn: 'root'
})
export class UiPrefsService {
  private destroyRef = inject(DestroyRef)

  uiPrefs!: UiPrefs;

  constructor(private uiCleanup: UiCleanupService,
              private filter: FilterService,
              private dialog: MatDialog) { }

  public loadUiPrefs() {
    try {
      const localPrefs = localStorage.getItem(UI_PREFS_KEY);
      if(!localPrefs) {
        return {
          clickedRowIds: new Set<number>(),
          lastClickedClean: 0,
          hideHiddenListingHint: false,
          hideReportedListingHint: false,
          hideBookmarkedListingHint: false,
          hideQuickAccessMenuHint: false,
          storedFilters: this.filter.pullState()
        };
      }
      const parsed = JSON.parse(localPrefs) as Partial<UiPrefs>
      return {
        clickedRowIds: new Set<number>(parsed.clickedRowIds ?? []),
        lastClickedClean: parsed.lastClickedClean ?? 0,
        hideHiddenListingHint: parsed.hideHiddenListingHint ?? false,
        hideReportedListingHint: parsed.hideReportedListingHint ?? false,
        hideBookmarkedListingHint: parsed.hideBookmarkedListingHint ?? false,
        hideQuickAccessMenuHint: parsed.hideQuickAccessMenuHint ?? false,
        storedFilters: parsed.storedFilters ?? this.filter.pullState()
      };

    } catch {
      localStorage.removeItem(UI_PREFS_KEY);
      return {
        clickedRowIds: new Set<number>([]),
        lastClickedClean: 0,
        hideHiddenListingHint: false,
        hideReportedListingHint: false,
        hideBookmarkedListingHint: false,
        hideQuickAccessMenuHint: false,
        storedFilters: this.filter.pullState()
      }
    }
  }

  clickedCleanupCheck() {
    const lastClean = this.uiPrefs.lastClickedClean;

    if((Date.now() - lastClean >= ONE_DAY_MS) || (this.uiPrefs.clickedRowIds.size >= SOFT_MAX_CLICKED)) {
      this.uiCleanup.cleanClickedListings(Array.from(this.uiPrefs.clickedRowIds)).pipe(
        takeUntilDestroyed(this.destroyRef)).subscribe({
        next: (response: {cleaned: number[] }) => {
          this.uiPrefs.clickedRowIds = new Set(response.cleaned);
          this.uiPrefs.lastClickedClean = Date.now();
          this.saveUiPrefs(this.uiPrefs);
        }
      })
    }
  }

  saveRowClick(row: number) {
    if (this.uiPrefs.clickedRowIds.size >= MAX_CLICKED) {
      let removed = 0;
      for(const oldest of Array.from(this.uiPrefs.clickedRowIds)) {
        this.uiPrefs.clickedRowIds.delete(oldest);
        removed++;
        if(removed >= CLICKED_EVICT_COUNT) break;
      }
    }

    if(this.uiPrefs.clickedRowIds.has(row)) {
      this.uiPrefs.clickedRowIds.delete(row);
    }

    this.uiPrefs.clickedRowIds.add(row);

    this.saveUiPrefs(this.uiPrefs);
  }

  displayBookmarkListingHint() {
    if (!this.uiPrefs.hideBookmarkedListingHint) {
      const dialogRef = this.dialog.open(DontShowMeAgainPopup, {
        data: {
          message: "<p>This listing has been added to your bookmarks.</p>" +
            "<p>Bookmarks can be accessed by visiting your 'Profile' " +
            "page and viewing the 'Bookmarks' tab.</p>"
        }
      });

      dialogRef.afterClosed().pipe(takeUntilDestroyed(this.destroyRef)).subscribe(dontShow => {
        if (dontShow) {
          this.uiPrefs.hideBookmarkedListingHint = true;
          this.saveUiPrefs(this.uiPrefs);
        }
      })
    }
  }

  displayHideListingHint() {
    if(!this.uiPrefs.hideHiddenListingHint) {
      const dialogRef = this.dialog.open(DontShowMeAgainPopup, {
        data: {
          message: "<p>This listing has been hidden and will no longer appear in your search results.</p>" +
            "<p>To unhide listings, use the 'Hidden' menu to the right above the listings table.</p>"
        }
      });

      dialogRef.afterClosed().pipe(takeUntilDestroyed(this.destroyRef)).subscribe(dontShow => {
        if (dontShow) {
          this.uiPrefs.hideHiddenListingHint = true;
          this.saveUiPrefs(this.uiPrefs);
        }
      })
    }
  }

  displayReportedListingHint() {
    if(!this.uiPrefs.hideReportedListingHint) {
      const dialogRef = this.dialog.open(DontShowMeAgainPopup, {
        data: {
          message: "<p>Listing Reported.</p>" +
            "<p>Reported listings will no longer appear in your search results. This action cannot be undone.</p>" +
            "<p>If you just want to hide a particular listing, use the 'hide' feature instead. Hide actions can " +
            "be undone. </p>"
        }
      });

      dialogRef.afterClosed().pipe(takeUntilDestroyed(this.destroyRef)).subscribe(dontShow => {
        if (dontShow) {
          this.uiPrefs.hideReportedListingHint = true;
          this.saveUiPrefs(this.uiPrefs);
        }
      })
    }
  }

  displayQuickAccessMenuHint() {
    if(!this.uiPrefs.hideQuickAccessMenuHint) {
      const dialogRef = this.dialog.open(DontShowMeAgainPopup, {
        data: {
          message: "<p><strong>Quick Access Menu:</strong> Right-click the row on desktop, or long press on touch screen.</p>" +
            "<p><strong>Detailed Popup:</strong> Left-click the row on desktop, or tap on touch screen.</p>"
        }
      });

      dialogRef.afterClosed().pipe(takeUntilDestroyed(this.destroyRef)).subscribe(dontShow => {
        if (dontShow) {
          this.uiPrefs.hideQuickAccessMenuHint = true;
          this.saveUiPrefs(this.uiPrefs);
        }
      })
    }
  }

  public saveUiPrefs(prefs: UiPrefs): void {
    localStorage.setItem(UI_PREFS_KEY, JSON.stringify({
      ...prefs,
      clickedRowIds: Array.from(prefs.clickedRowIds),
    }));
  }
}
