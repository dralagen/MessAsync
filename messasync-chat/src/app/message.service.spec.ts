import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { MessageService, Message } from './message.service';

describe('MessageService', () => {
  let service: MessageService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(MessageService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('sendMessage', () => {
    it('should send message with correct channel', () => {
      const testMessage = new Message('Hello', 'emoji');
      const expectedResponse = {
        id: '123',
        body: 'Hello',
        channel: 'emoji',
        createdAt: new Date()
      };

      service.sendMessage(testMessage).subscribe(response => {
        expect(response).toEqual(expectedResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/message');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(testMessage);
      req.flush(expectedResponse);
    });

    it('should send message with default channel', () => {
      const testMessage = new Message('Hello');
      
      service.sendMessage(testMessage).subscribe();

      const req = httpMock.expectOne('http://localhost:8080/message');
      expect(req.request.body.channel).toBe('default');
      req.flush({});
    });
  });

  describe('listenMessage', () => {
    it('should create EventSource with default channel', () => {
      // This is a basic test since EventSource is harder to mock
      // In a real scenario, you'd want to mock EventSource
      const observable = service.listenMessage();
      expect(observable).toBeDefined();
    });

    it('should create EventSource with specific channel', () => {
      const observable = service.listenMessage('emoji');
      expect(observable).toBeDefined();
    });
  });
});

describe('Message', () => {
  it('should create with message and default channel', () => {
    const message = new Message('Hello');
    expect(message.message).toBe('Hello');
    expect(message.channel).toBe('default');
  });

  it('should create with message and specific channel', () => {
    const message = new Message('Hello', 'emoji');
    expect(message.message).toBe('Hello');
    expect(message.channel).toBe('emoji');
  });
});
