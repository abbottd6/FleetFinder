import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatShellComponent } from './chat-shell.component';

describe('ChatComponent', () => {
  let component: ChatShellComponent;
  let fixture: ComponentFixture<ChatShellComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChatShellComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChatShellComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
