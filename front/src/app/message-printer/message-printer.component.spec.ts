import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessagePrinterComponent } from './message-printer.component';

describe('MessagePrinterComponent', () => {
  let component: MessagePrinterComponent;
  let fixture: ComponentFixture<MessagePrinterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessagePrinterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MessagePrinterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
