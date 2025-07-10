import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

import { MessageWriterComponent } from './message-writer.component';
import { MessageService } from '../message.service';

describe('MessageWriterComponent', () => {
  let component: MessageWriterComponent;
  let fixture: ComponentFixture<MessageWriterComponent>;
  let messageService: jasmine.SpyObj<MessageService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('MessageService', ['sendMessage']);

    await TestBed.configureTestingModule({
      imports: [MessageWriterComponent, ReactiveFormsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: MessageService, useValue: spy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MessageWriterComponent);
    component = fixture.componentInstance;
    messageService = TestBed.inject(MessageService) as jasmine.SpyObj<MessageService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with default values', () => {
    expect(component.messageForm.get('message')?.value).toBe('');
    expect(component.messageForm.get('channel')?.value).toBe('default');
  });

  it('should emit channelChange on form init', () => {
    spyOn(component.channelChange, 'emit');
    component.ngOnInit();
    expect(component.channelChange.emit).toHaveBeenCalledWith('default');
  });

  it('should emit channelChange when channel value changes', () => {
    spyOn(component.channelChange, 'emit');
    component.messageForm.get('channel')?.setValue('emoji');
    expect(component.channelChange.emit).toHaveBeenCalledWith('emoji');
  });

  it('should send message with correct channel', () => {
    const mockResponse = { id: '1', body: 'Test', channel: 'emoji', createdAt: new Date() };
    messageService.sendMessage.and.returnValue(of(mockResponse));
    
    component.messageForm.patchValue({
      message: 'Test message',
      channel: 'emoji'
    });
    
    component.sendMessage();
    
    expect(messageService.sendMessage).toHaveBeenCalledWith(
      jasmine.objectContaining({
        message: 'Test message',
        channel: 'emoji'
      })
    );
  });

  it('should reset form with current channel after successful send', () => {
    const mockResponse = { id: '1', body: 'Test', channel: 'admin', createdAt: new Date() };
    messageService.sendMessage.and.returnValue(of(mockResponse));
    
    component.messageForm.patchValue({
      message: 'Test message',
      channel: 'admin'
    });
    
    component.sendMessage();
    
    expect(component.messageForm.get('message')?.value).toBe('');
    expect(component.messageForm.get('channel')?.value).toBe('admin');
  });

  it('should handle send error', () => {
    messageService.sendMessage.and.returnValue(throwError('Error'));
    spyOn(console, 'error');
    
    component.messageForm.patchValue({
      message: 'Test message',
      channel: 'default'
    });
    
    component.sendMessage();
    
    expect(console.error).toHaveBeenCalledWith('Error sending message:', 'Error');
    expect(component.messageForm.enabled).toBe(true);
  });

  it('should not send message if form is invalid', () => {
    component.messageForm.patchValue({
      message: '',
      channel: 'default'
    });
    
    component.sendMessage();
    
    expect(messageService.sendMessage).not.toHaveBeenCalled();
  });

  it('should have channel selector with correct options', () => {
    const compiled = fixture.nativeElement;
    const channelSelect = compiled.querySelector('select[formControlName="channel"]');
    const options = channelSelect.querySelectorAll('option');
    
    expect(options.length).toBe(3);
    expect(options[0].value).toBe('default');
    expect(options[1].value).toBe('emoji');
    expect(options[2].value).toBe('admin');
  });
});

