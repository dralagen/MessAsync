import { Component, signal } from '@angular/core';
import {FormsModule} from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import {MessagePrinterComponent} from './message-printer/message-printer.component';
import {MessageWriterComponent} from './message-writer/message-writer.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, FormsModule, MessageWriterComponent, MessagePrinterComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'MessAsync Chat';
  selectedChannel = signal<string>('default');

  onChannelChange(channel: string) {
    this.selectedChannel.set(channel);
  }
}
