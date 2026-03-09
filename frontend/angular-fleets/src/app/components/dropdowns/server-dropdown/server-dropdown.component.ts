import {Component, Input, OnInit, ChangeDetectorRef, AfterViewInit} from '@angular/core';
import {LookupService} from "../../../services/api-services/reference-data-api/lookup.service";
import {catchError, of} from "rxjs";
import {FormControl} from "@angular/forms";
import {environment} from "../../../../environments/environment";
import {ListingFormService} from "../../../services/listing-form-service/listing-form.service";
import {ServerRegion} from "../../../models/reference-data/reference-data.models";

@Component({
    selector: 'app-server-dropdown',
    templateUrl: './server-dropdown.component.html',
    styleUrl: './server-dropdown.component.css',
    standalone: false
})
export class ServerDropdownComponent implements AfterViewInit {
  @Input() serverControl!: FormControl;
  servers: ServerRegion[] = [];

  constructor(private lookupService: LookupService,
              private cdr: ChangeDetectorRef,
              private formService: ListingFormService) {}

  ngAfterViewInit(): void {
    this.fetchServerRegions();
  }

  fetchServerRegions(): void {
    this.lookupService.getServerRegions()
      .pipe(
        catchError((err) => {
          console.error('Error fetching dropdown server regions:', err);
          return of([]);
        })
      )
      .subscribe({
        next: (data) => {
          this.servers = data;
          this.cdr.markForCheck();
          if (!environment.production) {
            console.log('Server dropdown options fetched:', this.servers);
          }
        },
        complete: () => {
          const userTz = Intl.DateTimeFormat().resolvedOptions().timeZone;
          const region = this.defaultRegionFromTimeZone(userTz);
          if(region) {
            this.formService.serverRegion.patchValue(region);
          }
        }
      })
  }

  defaultRegionFromTimeZone(tz: String | null): number | null {
    if(!tz) {
      return this.findServerByName('Any');
    }

    const region = tz.split('/')[0];

    switch (region) {
      case 'America': return this.findServerByName('USA');
      case 'Europe': return this.findServerByName('EU');
      case 'Australia': return this.findServerByName('AUS')
      case 'Asia': return this.findServerByName('Asia');
      default: return this.findServerByName('Unknown');
    }
  }

  findServerByName(name: string): number | null {
    if(!name) return null;

    return this.servers.find(server => server.servername === name)?.serverId || null;
  }

  timeZones: { label: string, value: string }[] = [
    { label: 'UTC', value: 'UTC' },
    { label: 'Eastern Time (ET)', value: 'America/New_York' },
    { label: 'Central Time (CT)', value: 'America/Chicago' },
    { label: 'Mountain Time (MT)', value: 'America/Denver' },
    { label: 'Pacific Time (PT)', value: 'America/Los_Angeles' },
    { label: 'Brazil (BRT)', value: 'America/Sao_Paulo' },
    { label: 'Greenwich Mean Time (GMT)', value: 'Europe/London' },
    { label: 'Central European Time (CET)', value: 'Europe/Paris' },
    { label: 'Moscow Time', value: 'Europe/Moscow' },
    { label: 'Gulf Standard Time (GST)', value: 'Asia/Dubai' },
    { label: 'India Standard Time (IST)', value: 'Asia/Kolkata' },
    { label: 'China Standard Time (CST)', value: 'Asia/Shanghai' },
    { label: 'Japan Standard Time (JST)', value: 'Asia/Tokyo' },
    { label: 'Australian Eastern Time (AET)', value: 'Australia/Sydney' },
    { label: 'New Zealand Time', value: 'Pacific/Auckland' },
    { label: 'South Africa Standard Time', value: 'Africa/Johannesburg' },
    { label: 'Eastern European Time (EET)', value: 'Africa/Cairo' }
  ]
}
