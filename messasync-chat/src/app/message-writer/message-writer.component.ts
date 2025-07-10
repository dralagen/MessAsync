import {Component, EventEmitter, inject, OnInit, Output} from '@angular/core';
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
  @Output() channelChange = new EventEmitter<string>();
  messageForm!: FormGroup; // Added "!" non-null assertion operator
  private sendMessageService = inject(MessageService);
  private fb = inject(FormBuilder);

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.messageForm = this.fb.group({
      message: ['', Validators.required],
      channel: ['default', Validators.required]
    });
    
    // Emit initial channel
    this.channelChange.emit('default');
    
    // Listen for channel changes
    this.messageForm.get('channel')?.valueChanges.subscribe(channel => {
      this.channelChange.emit(channel);
    });
  }

  sendMessage(): void {
    if (this.messageForm.valid) {
      this.messageForm.disable()
      console.log("Sending message", this.messageForm.value.message);
      const message = new Message(this.messageForm.value.message, this.messageForm.value.channel);

      this.sendMessageService.sendMessage(message)
        .subscribe({
          next: () => {
            this.messageForm.reset({
              message: '',
              channel: this.messageForm.value.channel
            });
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
