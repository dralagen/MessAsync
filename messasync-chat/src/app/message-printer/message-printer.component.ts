import {DatePipe} from '@angular/common';
import {Component, Input, OnChanges, signal, SimpleChanges} from '@angular/core';
import {Subscription} from 'rxjs';
import {MessageResponse, MessageService} from '../message.service';

@Component({
  selector: 'app-message-printer',
    imports: [
        DatePipe
    ],
  templateUrl: './message-printer.component.html',
  styleUrl: './message-printer.component.css'
})
export class MessagePrinterComponent implements OnChanges {
  @Input() selectedChannel: string = 'default';
  
  constructor(public messageService: MessageService) {
  }
  messages = signal<MessageResponse[]>([]);
  subscription: Subscription | undefined;

  ngOnInit() {
    this.subscribeToChannel(this.selectedChannel);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['selectedChannel'] && !changes['selectedChannel'].firstChange) {
      this.subscribeToChannel(this.selectedChannel);
      this.clear(); // Clear messages when switching channels
    }
  }

  private subscribeToChannel(channel: string) {
    this.subscription?.unsubscribe();
    this.subscription = this.messageService.listenMessage(channel).subscribe(message => {
      this.messages.update(messages => [...messages, message]);
    });
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

  clear() {
    this.messages.update(() => []);
  }
}
