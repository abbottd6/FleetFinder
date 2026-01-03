import {Component, DestroyRef, EventEmitter, inject, Output, ViewChild} from '@angular/core';
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {MatError, MatFormField, MatHint} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {NgIf} from "@angular/common";
import {CdkTextareaAutosize} from "@angular/cdk/text-field";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";
import {ChatHostService} from "../../../services/facade-services/chat/chat-host.service";
import {distinctUntilChanged, filter} from "rxjs";
import {ChatWindowState} from "../shell-component/chat-shell.component";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

@Component({
  selector: 'app-message-input',
  standalone: true,
  templateUrl: './message-input.component.html',
  imports: [
    MatFormField,
    MatHint,
    MatInput,
    NgIf,
    ReactiveFormsModule,
    CdkTextareaAutosize,
    MatIcon,
    MatButton
  ],
  styleUrl: './message-input.component.css'
})
export class MessageInputComponent {
  @ViewChild(CdkTextareaAutosize) autosize?: CdkTextareaAutosize;
  ChatWindowState = ChatWindowState;
  private inputDestroyRef = inject(DestroyRef);
  protected isExpanding: boolean = false;

  @Output() sendMessage: EventEmitter<string> = new EventEmitter<string>();

  inputCtrl: FormControl<string | null> = new FormControl<string | null>(null);
  characterCount: number = 0;

  updateCharacterCount() {
    const value = this.inputCtrl.value || '';
    this.characterCount = value.length;
  }

  constructor(private chatHostSrv: ChatHostService) {
    this.chatHostSrv.windowState$.pipe(
      distinctUntilChanged(),
      filter(state => state === ChatWindowState.expanded),
      takeUntilDestroyed(this.inputDestroyRef))
      .subscribe((state) => {
        this.isExpanding = true;
        setTimeout(() => this.isExpanding = false, 220);
      })
  }

  emitMessage(input: string | null) {
    if(input) {
      this.sendMessage.emit(input);
      this.inputCtrl.reset();
    }
  }
  inputEnter(event: Event) {
    event.stopPropagation();
    event.preventDefault();
    const keyEvent = event as KeyboardEvent;
    if(keyEvent.shiftKey) return;

    const value = (event.target as HTMLInputElement).value;
    this.emitMessage(value);
  }
}
