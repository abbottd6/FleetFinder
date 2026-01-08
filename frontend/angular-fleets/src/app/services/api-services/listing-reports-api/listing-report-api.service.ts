import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../environments/environment";
import {BehaviorSubject, combineLatest, map, Observable, of, shareReplay, switchMap, tap} from "rxjs";
import {AuthService} from "../../auth/auth-services/auth.service";
import {SubmitListingReport} from "../../../models/report-requests/submit-listing-report";

export interface reportOption {
  id: number,
  option: string
}

@Injectable({
  providedIn: 'root'
})
export class ListingReportApiService {

  private submitReportUrl = `${environment.apiBaseUrl}/users/group_listings/submit_report`;
  private reportBasisUrl = `${environment.apiBaseUrl}/lookup/report-basis`;

  public reportOptions$!: Observable<reportOption[]>;

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

  submitReport(report: SubmitListingReport) {
    return this.httpClient.post<any>(this.submitReportUrl, report)
  }

  getListingReportBasisApi() {
    return this.httpClient.get<any>(this.reportBasisUrl);
  }
}
