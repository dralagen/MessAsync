import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageWriterComponent } from './message-writer.component';

describe('MessageWriterComponent', () => {
  let component: MessageWriterComponent;
  let fixture: ComponentFixture<MessageWriterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageWriterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MessageWriterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
