import {DatePipe} from '@angular/common';
import {Component, signal} from '@angular/core';
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
export class MessagePrinterComponent {
  constructor(public messageService: MessageService) {
  }
  messages = signal<MessageResponse[]>([]);
  subscription: Subscription | undefined;

  ngOnInit() {
    this.subscription = this.messageService.listenMessage().subscribe(message => {
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
