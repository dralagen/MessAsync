import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';


export class Message {
  constructor(public message: string, public channel: string = "default") {
  }
}

export class MessageResponse {
  constructor(public id: string, public message: string, public channel: string, public createdAt: Date) {
  }
}

class CreatedEventMessage {
  constructor(public id: string, public body: string, public channel: string, public createdAt: Date) {
  }
}

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  constructor(private http: HttpClient) {
  }

  sendMessage(message: Message) {
    return this.http.post<CreatedEventMessage>("http://localhost:8080/message", message)
  }

  listenMessage(): Observable<MessageResponse> {
    return new Observable<MessageResponse>(observer => {
        const eventSource = new EventSource('http://localhost:8080/message/event');
        eventSource.addEventListener("createdMessage", (event) => {
          console.log("Received message", event.data);
          let createdMessage = JSON.parse(event.data) as MessageResponse;

          observer.next(createdMessage);
        });

        eventSource.onerror = (event) => {
          console.log("Received error", event);
        }
        return () => {
          eventSource.close();
        }
      }
    )
  }
}
