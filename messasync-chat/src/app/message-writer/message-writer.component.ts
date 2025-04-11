import {Component, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Message, MessageService} from '../message.service';

@Component({
  selector: 'app-message-writer',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './message-writer.component.html',
  styleUrl: './message-writer.component.css'
})
export class MessageWriterComponent implements OnInit {
  messageForm!: FormGroup; // Added "!" non-null assertion operator
  private sendMessageService = inject(MessageService);
  private fb = inject(FormBuilder);

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.messageForm = this.fb.group({
      message: ['', Validators.required]
    });
  }

  sendMessage(): void {
    if (this.messageForm.valid) {
      this.messageForm.disable()
      console.log("Sending message", this.messageForm.value.message);
      const message = new Message(this.messageForm.value.message, "default");

      this.sendMessageService.sendMessage(message)
        .subscribe({
          next: () => {
            this.messageForm.reset();
            this.messageForm.enable();
          },
          error: (error) => {
            console.error('Error sending message:', error);
            this.messageForm.enable()
          }
        });
    }
  }
}
