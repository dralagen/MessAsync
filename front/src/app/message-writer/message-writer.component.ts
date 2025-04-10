import {DatePipe} from '@angular/common';
import {Component, inject, model, Output, signal} from '@angular/core';
import {FormsModule, NgForm} from '@angular/forms';
import {Subscription} from 'rxjs';
import {Message, MessageService} from '../message.service';

@Component({
  selector: 'app-message-writer',
  imports: [
    FormsModule,
    DatePipe
  ],
  templateUrl: './message-writer.component.html',
  styleUrl: './message-writer.component.css'
})
export class MessageWriterComponent {

  private sendMessageService = inject(MessageService)

  sendMessage(form: NgForm) {
    if (form.valid) {
      console.log(form.value.message);
      let message = new Message(form.value.message, "default")
      this.sendMessageService.sendMessage(message)
        .subscribe(() => {
          form.reset();
        });

    }
  }

}
