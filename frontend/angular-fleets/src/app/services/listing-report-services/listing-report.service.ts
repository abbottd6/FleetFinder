import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {BehaviorSubject, combineLatest, Observable, of, shareReplay, switchMap, tap} from "rxjs";
import {AuthService} from "../auth/auth-services/auth.service";
import {SubmitListingReport} from "../../models/report-requests/submit-listing-report";

@Injectable({
  providedIn: 'root'
})
export class ListingReportService {

  private submitReportUrl = `${environment.apiBaseUrl}/users/group_listings/submit_report`;
  private getReportBriefUrl = `${environment.apiBaseUrl}/users/group_listings/report_brief`;
  private auth = inject(AuthService);

  private refreshReportsSubject = new BehaviorSubject<void>(undefined);
  readonly refreshUserReports$ = this.refreshReportsSubject.asObservable();
  readonly userReportsBrief$: Observable<number[]> = combineLatest([
    this.auth.isLoggedIn$,
    this.refreshUserReports$
  ]).pipe(
    switchMap(([isLoggedIn]) =>
      isLoggedIn ? this.getUserReportsBrief()
        : of<number[]>([])
    ),
    shareReplay(1)
  );

  constructor(private httpClient: HttpClient) { }

  getUserReportsBrief(): Observable<any> {
    return this.httpClient.get<any>(this.getReportBriefUrl).pipe(
      tap(response => console.log("getUserReportsBrief response: ", response))
    )
  }

  submitReport(report: SubmitListingReport) {
    return this.httpClient.post<any>(this.submitReportUrl, report).pipe(
      tap(() => this.triggerReportsBriefRefresh())
    )
  }

  private triggerReportsBriefRefresh(): void {
    this.refreshReportsSubject.next();
  }
}
