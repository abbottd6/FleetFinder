import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {BehaviorSubject, combineLatest, map, Observable, of, shareReplay, switchMap, tap} from "rxjs";
import {AuthService} from "../auth/auth-services/auth.service";
import {SubmitListingReport} from "../../models/report-requests/submit-listing-report";

export interface reportOption {
  id: number,
  option: string
}

@Injectable({
  providedIn: 'root'
})
export class ListingReportService {

  private submitReportUrl = `${environment.apiBaseUrl}/users/group_listings/submit_report`;
  private getReportBriefUrl = `${environment.apiBaseUrl}/users/group_listings/report_brief`;
  private auth = inject(AuthService);
  private reportBasisUrl = `${environment.apiBaseUrl}/lookup/report-basis`;

  public reportOptions$!: Observable<reportOption[]>;
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

  constructor(private httpClient: HttpClient) {

    const observe = this.getListingReportBasisApi();
    this.reportOptions$ = observe.pipe(
      map(arr =>
      arr.map((data: { basisId: number; basisLabel: string; }) => ({
        id: data.basisId,
        option: data.basisLabel
      }))
    )
    )
  }

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

  getListingReportBasisApi() {
    return this.httpClient.get<any>(this.reportBasisUrl);
  }

  private triggerReportsBriefRefresh(): void {
    this.refreshReportsSubject.next();
  }
}
