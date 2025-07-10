import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { SimpleChanges } from '@angular/core';
import { of, Subject } from 'rxjs';

import { MessagePrinterComponent } from './message-printer.component';
import { MessageService, MessageResponse } from '../message.service';

describe('MessagePrinterComponent', () => {
  let component: MessagePrinterComponent;
  let fixture: ComponentFixture<MessagePrinterComponent>;
  let messageService: jasmine.SpyObj<MessageService>;
  let messageSubject: Subject<MessageResponse>;

  beforeEach(async () => {
    messageSubject = new Subject<MessageResponse>();
    const spy = jasmine.createSpyObj('MessageService', ['listenMessage']);
    spy.listenMessage.and.returnValue(messageSubject.asObservable());

    await TestBed.configureTestingModule({
      imports: [MessagePrinterComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: MessageService, useValue: spy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MessagePrinterComponent);
    component = fixture.componentInstance;
    messageService = TestBed.inject(MessageService) as jasmine.SpyObj<MessageService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default channel', () => {
    component.ngOnInit();
    expect(messageService.listenMessage).toHaveBeenCalledWith('default');
  });

  it('should subscribe to specific channel', () => {
    component.selectedChannel = 'emoji';
    component.ngOnInit();
    expect(messageService.listenMessage).toHaveBeenCalledWith('emoji');
  });

  it('should resubscribe when channel changes', () => {
    component.selectedChannel = 'default';
    component.ngOnInit();
    expect(messageService.listenMessage).toHaveBeenCalledWith('default');
    
    // Simulate channel change
    const changes: SimpleChanges = {
      selectedChannel: {
        currentValue: 'emoji',
        previousValue: 'default',
        firstChange: false,
        isFirstChange: () => false
      }
    };
    
    component.selectedChannel = 'emoji';
    component.ngOnChanges(changes);
    
    expect(messageService.listenMessage).toHaveBeenCalledWith('emoji');
  });

  it('should clear messages when channel changes', () => {
    component.selectedChannel = 'default';
    component.ngOnInit();
    
    // Add some messages
    const testMessage = new MessageResponse('1', 'Hello', 'default', new Date());
    messageSubject.next(testMessage);
    
    expect(component.messages().length).toBe(1);
    
    // Simulate channel change
    spyOn(component, 'clear');
    const changes: SimpleChanges = {
      selectedChannel: {
        currentValue: 'emoji',
        previousValue: 'default',
        firstChange: false,
        isFirstChange: () => false
      }
    };
    
    component.selectedChannel = 'emoji';
    component.ngOnChanges(changes);
    
    expect(component.clear).toHaveBeenCalled();
  });

  it('should add messages to the list', () => {
    component.ngOnInit();
    
    const testMessage1 = new MessageResponse('1', 'Hello', 'default', new Date());
    const testMessage2 = new MessageResponse('2', 'World', 'default', new Date());
    
    messageSubject.next(testMessage1);
    expect(component.messages().length).toBe(1);
    expect(component.messages()[0]).toEqual(testMessage1);
    
    messageSubject.next(testMessage2);
    expect(component.messages().length).toBe(2);
    expect(component.messages()[1]).toEqual(testMessage2);
  });

  it('should clear all messages', () => {
    component.ngOnInit();
    
    // Add some messages
    const testMessage = new MessageResponse('1', 'Hello', 'default', new Date());
    messageSubject.next(testMessage);
    expect(component.messages().length).toBe(1);
    
    // Clear messages
    component.clear();
    expect(component.messages().length).toBe(0);
  });

  it('should unsubscribe on destroy', () => {
    component.ngOnInit();
    expect(component.subscription).toBeDefined();
    
    spyOn(component.subscription!, 'unsubscribe');
    component.ngOnDestroy();
    
    expect(component.subscription!.unsubscribe).toHaveBeenCalled();
  });

  it('should not process changes on first change', () => {
    const changes: SimpleChanges = {
      selectedChannel: {
        currentValue: 'emoji',
        previousValue: undefined,
        firstChange: true,
        isFirstChange: () => true
      }
    };
    
    component.ngOnChanges(changes);
    
    // Should not call listenMessage again since it's first change
    expect(messageService.listenMessage).not.toHaveBeenCalled();
  });
});
