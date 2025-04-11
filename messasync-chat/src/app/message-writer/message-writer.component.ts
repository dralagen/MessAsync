import {Component, inject} from '@angular/core';
import {FormsModule, NgForm} from '@angular/forms';
import {Message, MessageService} from '../message.service';

@Component({
  selector: 'app-message-writer',
  imports: [
    FormsModule
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
