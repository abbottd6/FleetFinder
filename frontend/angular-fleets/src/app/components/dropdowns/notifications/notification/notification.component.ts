import {AfterViewInit, Component, Input} from '@angular/core';
import {NotificationViewModel} from "../../../../models/NotificationViewModel";
import {SlicePipe} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {NotificationService} from "../../../../services/facade-services/notifications/notification.service";

@Component({
  selector: 'app-notification',
  standalone: true,
  templateUrl: './notification.component.html',
  imports: [
    SlicePipe,
    MatIcon
  ],
  styleUrl: './notification.component.css'
})
export class NotificationComponent implements AfterViewInit {
  @Input() note!: NotificationViewModel;

  protected header!: String | undefined;
  protected message!: String | undefined;

  constructor(protected noteService: NotificationService) {}

  ngAfterViewInit(): void {
    this.buildNoteDisplay();
  }

  buildNoteDisplay() {
    if(this.note.type === 'MOD_DELETE') {
      this.header = "Listing removed by a moderator"
      this.message = `Basis for removal: `
    }
  }
}
