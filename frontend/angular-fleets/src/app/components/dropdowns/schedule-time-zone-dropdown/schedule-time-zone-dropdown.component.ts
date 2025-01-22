import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-schedule-time-zone-dropdown',
  standalone: false,

  templateUrl: './schedule-time-zone-dropdown.component.html',
  styleUrl: './schedule-time-zone-dropdown.component.css'
})
export class ScheduleTimeZoneDropdownComponent implements AfterViewInit{
  @Input() eventScheduleZoneControl!: FormControl;
  @Input() groupStatusControl!: FormControl;

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

  ngAfterViewInit() {

    //subscribing to groupStatus dropdown for enable/disable of event time zone when groupStatus value changes
    //event time zone should only be enabled when groupStatus is set to "future/scheduled"
    this.groupStatusControl?.valueChanges.subscribe(value => {
      if (value == 2) {
        this.eventScheduleZoneControl?.enable();

        //auto-detecting and setting user time zone
        const detectedTimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
        this.setTimeZone(detectedTimeZone);

        //disabling and resetting time zone if group status != "future/scheduled"
      } else {
        this.eventScheduleZoneControl?.reset();
        this.eventScheduleZoneControl?.disable();
      }
    });
  }

  //matches detected time zone to time zone dropdown option
  setTimeZone(timeZone: string): void {
    const optionsMatchedTimeZone = this.timeZones.find(option => option.value === timeZone);
    if (optionsMatchedTimeZone) {
      this.eventScheduleZoneControl.setValue(optionsMatchedTimeZone.value);
    }
  }
}
