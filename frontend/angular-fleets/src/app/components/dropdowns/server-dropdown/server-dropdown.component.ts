import {Component, Input, OnInit, Output, EventEmitter} from '@angular/core';
import {LookupService} from "../../../services/lookup.service";
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-server-dropdown',
  templateUrl: './server-dropdown.component.html',
  styleUrl: './server-dropdown.component.css'
})
export class ServerDropdownComponent implements OnInit {
  @Input() selectedValue: number | null = null;
  @Output() valueChange = new EventEmitter<number>();

  public servers: any[] = [];
  loading = false; //spinner animation

  constructor(private lookupService: LookupService) {}

  ngOnInit(): void {
    this.fetchServerRegions();
  }

  fetchServerRegions(): void {
    this.loading = true;
    this.lookupService.getServerRegions().subscribe({
      next: data => {
        this.servers = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching server regions:', error);
        this.loading = false;
      },
      complete: () => {
        console.log('Fetching complete.');
      }
    });
  }

  onValueChange(value: number): void {
    this.valueChange.emit(value);
  }
}
